package com.music.service.music;

import com.music.adaptor.FileStorage;
import com.music.constatns.AudioQuality;
import com.music.constatns.FileType;
import com.music.constatns.LyricsMimeType;
import com.music.convert.AudioQualityConverter;
import com.music.dto.music.MusicUpdateRequest;
import com.music.dto.music.MusicUpdateResponse;
import com.music.dto.upload.AudioFileInfo;
import com.music.dto.upload.FileKeys;
import com.music.dto.upload.LyricsUpload;
import com.music.dto.upload.MusicUpload;
import com.music.eneity.Lyrics;
import com.music.eneity.Music;
import com.music.eneity.constants.LyricsFormat;
import com.music.repository.LyricsRepository;
import com.music.repository.MusicRepository;
import com.music.util.FileUtils;
import com.music.validator.BusinessValidator;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicUpdateService {

  private final MusicRepository musicRepository;
  private final LyricsRepository lyricsRepository;
  private final AudioQualityConverter audioQualityConverter;
  private final FileStorage fileStorage;
  private final BusinessValidator businessValidator;

  @Transactional
  public MusicUpdateResponse updateMusic(
      Long musicId,
      MusicUpdateRequest request,
      @Nullable MultipartFile musicFile,
      @Nullable MultipartFile lyricsFile
  ) {
    Music music = getMusic(musicId); // 음원 조회
    BusinessValidator.validateReleaseStatus(music.getReleaseStatus()); // 검증
    updateMusicFilesIfExist(musicFile, music, request); // 음원 업데이트

    // TODO: update - 1쿼리, 가사 검색 - 2쿼리, 가사 저장 - 3쿼리.
    // TODO: 총 3개의 쿼리 실행됨. 최적화 필요.

    Lyrics lyrics = updateLyricsFileIfExist(musicId, lyricsFile, music); // 가사 업데이트
    return MusicUpdateResponse.of(music, lyrics);
  }

  private Lyrics updateLyricsFileIfExist(
      Long musicId,
      @Nullable MultipartFile lyricFile,
      Music music
  ) {
    if (lyricFile == null) {
      log.info("요청된 가사 파일 없음");
      return null;
    }
    log.info("요청된 가사 파일 존재 musicId : {}", musicId);
    businessValidator.validateFile(lyricFile, FileType.LYRICS); // 가사 비즈니스 규칙 검증
    return lyricsRepository.findByMusicId(musicId)
        .map(existLyrics -> updateExistLyrics(lyricFile, existLyrics))
        .orElseGet(() -> createNewLyrics(lyricFile, music));
  }

  private Lyrics updateExistLyrics(MultipartFile lyricFile, Lyrics existLyrics) {
    String newFileKey = uploadLyricsFile(lyricFile); // 새로운 가사 파일 업로드 at S3
    tryDeleteLyricsFile(existLyrics); // 기존 가사 파일 삭제 시도 at S3
    updateLyricsInfo(lyricFile, existLyrics, newFileKey); // 가사 업데이트
    return existLyrics;
  }

  private Lyrics createNewLyrics(MultipartFile lyricFile, Music music) {
    String newFileKey = uploadLyricsFile(lyricFile); // 새로운 가사 파일 업로드 at S3
    return lyricsRepository.save(createLyrics(lyricFile, music, newFileKey)); // 가사 저장 at RDB
  }

  private void tryDeleteLyricsFile(Lyrics existLyrics) {
    try {
      fileStorage.deleteFile(existLyrics.getLyricsFileKey());
    } catch (Exception e) { // 삭제 실패해도 계속 진행
      log.error("기존 가사 파일 삭제 실패 lyricsId : {}", existLyrics.getId());
      // TODO: 비즈니스 규칙 필요
    }
  }

  private static void updateLyricsInfo(
      MultipartFile lyricFile,
      Lyrics existLyrics,
      String newFileKey
  ) {
    LyricsFormat lyricsFormat = LyricsMimeType.getFormatByContentType(lyricFile.getContentType());
    existLyrics.updateLyricsFileKey(newFileKey, lyricsFormat); // RDB 가사 업데이트
  }

  private static Lyrics createLyrics(MultipartFile lyricFile, Music music, String newFileKey) {
    return Lyrics.builder()
        .music(music) // 모든 정보가 업데이트 된 후 셋팅
        .lyricsFileKey(newFileKey)
        .lyricsFormat(LyricsMimeType.getFormatByContentType(lyricFile.getContentType()))
        .build();
  }

  private void updateMusicFilesIfExist(
      @Nullable MultipartFile musicFile,
      Music music,
      MusicUpdateRequest request
  ) {
    if (musicFile != null) {
      updateMusicFileIfExist(musicFile, music);
    }
    music.update(
        request.getTitle(),
        request.getReleaseAt(),
        request.getGenre(),
        request.getTitleTrack(),
        request.getReleaseStatus()
    );
  }

  private void updateMusicFileIfExist(MultipartFile musicFile, Music music) {
    businessValidator.validateFile(musicFile, FileType.MUSIC);
    deleteCurrentMusicFiles(music);

    AudioFileInfo audioFileInfo = updateMusicFile(musicFile);
    updateNewMusicFileKeys(music, audioFileInfo);
  }

  private static void updateNewMusicFileKeys(Music music, AudioFileInfo audioFileInfo) {
    FileKeys musicFileKeys = audioFileInfo.getFileKeys();
    music.updateAudioFileInfo(
        musicFileKeys.getHighQualityKey(),
        musicFileKeys.getMediumQualityKey(),
        musicFileKeys.getLowQualityKey(),
        audioFileInfo.getDuration()
    );
  }

  private void deleteCurrentMusicFiles(Music music) {
    FileKeys currentFileKeys = FileKeys.of(
        music.getHighQualityFileKey(),
        music.getMediumQualityFileKey(),
        music.getLowQualityFileKey()
    );
    fileStorage.deleteFiles(currentFileKeys);
  }

  private AudioFileInfo updateMusicFile(MultipartFile musicFile) {
    try {
      Map<AudioQuality, File> convertedMusicFiles = audioQualityConverter.process(musicFile);
      int duration = audioQualityConverter.extractDuration(
          convertedMusicFiles.get(AudioQuality.HIGH)
      );
      MusicUpload musicUpload = MusicUpload.of(musicFile, duration, convertedMusicFiles);
      FileKeys fileKeys = fileStorage.uploadMusic(musicUpload);

      return AudioFileInfo.of(fileKeys, duration);
    } catch (IOException e) {
      log.error("오디오 파일 업로드 실패. {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  private String uploadLyricsFile(MultipartFile lyricsFile) {
    try {
      File file = FileUtils.convertMultipartFileToFile(lyricsFile);
      LyricsUpload lyricsUpload = LyricsUpload.of(lyricsFile, file);
      return fileStorage.uploadLyrics(lyricsUpload);
    } catch (IOException e) {
      log.error("가사 파일 MultipartFile to File 변환 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  @Transactional(readOnly = true)
  private Music getMusic(Long musicId) {
    return musicRepository.findById(musicId)
        .orElseThrow(() -> {
          log.error("업데이트 할 수 없는 음원 musicId : {}", musicId);
          return new RuntimeException(); // TODO: CustomException 으로 변경
        });
  }
}
