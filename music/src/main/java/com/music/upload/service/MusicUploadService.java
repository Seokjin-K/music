package com.music.upload.service;

import com.music.constatns.AudioQuality;
import com.music.constatns.FileType;
import com.music.constatns.LyricsMimeType;
import com.music.upload.convertor.AudioQualityConverter;
import com.music.upload.dto.MusicUpload;
import com.music.upload.dto.FileKeys;
import com.music.upload.dto.TrackUpload;
import com.music.upload.dto.LyricsUpload;
import com.music.upload.dto.MusicUploadResponse;
import com.music.upload.dto.MusicUploadRequest;
import com.music.eneity.Album;
import com.music.eneity.Lyrics;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.AlbumRepository;
import com.music.repository.LyricsRepository;
import com.music.repository.MusicRepository;
import com.music.adaptor.FileStorage;
import com.music.util.FileUtils;
import com.music.validator.BusinessValidator;
import com.music.validator.FileValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicUploadService {

  private final AlbumRepository albumRepository;
  private final MusicRepository musicRepository;
  private final LyricsRepository lyricsRepository;
  private final FileValidatorFactory fileValidatorFactory;
  private final AudioQualityConverter audioQualityConverter;
  private final FileStorage fileStorage;

  @Transactional
  //@Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
  @Scheduled(cron = "0 18 * * *") // 오후 6시 정각마다 실행
  public void releaseMusics() {
    int updateCount = musicRepository
        .updatePendingToReleasedByBeforeNow(ReleaseStatus.PENDING, ReleaseStatus.RELEASED);
    log.info("PENDING -> RELEASED Update {} queries", updateCount);
  }

  @Transactional
  public MusicUploadResponse uploadMusic(
      MusicUploadRequest request, MultipartFile musicFile, MultipartFile lyricFile) {

    Album album = getAlbum(request.getAlbumId());

    if (BusinessValidator.hasLyricsFile(lyricFile)) {
      fileValidatorFactory.getValidator(lyricFile, FileType.LYRICS);
    }
    fileValidatorFactory.getValidator(musicFile, FileType.MUSIC);

    FileKeys fileKeys = processAudioFileConversion(musicFile, lyricFile);

    Music music = createMusic(request, album, fileKeys);
    musicRepository.save(music);

    final String lyricsKey = fileKeys.getLyricsKey();
    if (BusinessValidator.hasLyricsFile(lyricFile)) {
      Lyrics lyrics = createLyrics(lyricFile, music, fileKeys);

      lyricsRepository.save(lyrics);
    }

    // TODO: 발매일 오후6에 'RELEASED' 상태 되도록 스케줄링 등록 필요

    return MusicUploadResponse.from(music, lyricsKey);
  }

  private FileKeys processAudioFileConversion(
      MultipartFile musicFile,
      @Nullable MultipartFile lyricsFile
  ) {
    try {
      TrackUpload trackUpload = createTrackUpload(musicFile, lyricsFile);
      return fileStorage.uploadTrack(trackUpload);
    } catch (IOException e) {
      log.error("오디오 파일 프로세싱 실패. {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경 필요
    }
  }

  private TrackUpload createTrackUpload(
      MultipartFile musicFile,
      @Nullable MultipartFile lyricsFile
  ) throws IOException {

    Map<AudioQuality, File> convertedMusicFiles = audioQualityConverter.process(musicFile);
    MusicUpload musicUpload = MusicUpload.of(musicFile, convertedMusicFiles);

    LyricsUpload lyricsUpload = createLyricsUploadIfExists(lyricsFile);
    return TrackUpload.of(musicUpload, lyricsUpload);
  }

  private static LyricsUpload createLyricsUploadIfExists(MultipartFile lyricsFile)
      throws IOException {
    LyricsUpload lyricsUpload = null;
    if (BusinessValidator.hasLyricsFile(lyricsFile)) {
      File convertedLyricsFile = FileUtils.convertMultipartFileToFile(lyricsFile);
      lyricsUpload = LyricsUpload.of(lyricsFile, convertedLyricsFile);
    }
    return lyricsUpload;
  }

  @Transactional(readOnly = true)
  private Album getAlbum(Long albumId) {
    Album album = albumRepository.findById(albumId)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경 필요

    BusinessValidator.validateReleaseStatus(album.getStatus());
    return album;
  }

  private Music createMusic(
      MusicUploadRequest request,
      Album album, FileKeys
      musicFileKeys
  ) {
    return Music.builder()
        .album(album)
        .highQualityFileKey(musicFileKeys.getHighQualityKey())
        .mediumQualityFileKey(musicFileKeys.getMediumQualityKey())
        .lowQualityFileKey(musicFileKeys.getLowQualityKey())
        .title(request.getTitle())
        .trackNumber(request.getTrackNumber())
        .duration(request.getDuration())
        .releaseAt(request.getReleaseAt())
        .genre(request.getGenre())
        .titleTrack(request.getTitleTrack())
        .releaseStatus(ReleaseStatus.PENDING)
        .build();
  }

  private static Lyrics createLyrics(
      MultipartFile lyricFile,
      Music music,
      FileKeys fileKeys
  ) {
    return Lyrics.builder()
        .music(music)
        .lyricsFileKey(fileKeys.getLyricsKey())
        .lyricsFormat(LyricsMimeType.getFormatByContentType(lyricFile.getContentType()))
        .build();
  }
}
