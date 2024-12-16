package com.music.service.music;

import com.music.constatns.AudioQuality;
import com.music.constatns.LyricsMimeType;
import com.music.convert.AudioQualityConverter;
import com.music.dto.upload.AudioFileInfo;
import com.music.dto.upload.MusicUpload;
import com.music.dto.upload.FileKeys;
import com.music.dto.upload.TrackUpload;
import com.music.dto.upload.LyricsUpload;
import com.music.dto.upload.MusicUploadResponse;
import com.music.dto.upload.MusicUploadRequest;
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
  private final BusinessValidator businessValidator;
  private final AudioQualityConverter audioQualityConverter;
  private final FileStorage fileStorage;

  @Transactional
  //@Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
  @Scheduled(cron = "0 0 18 * * *") // 오후 6시 정각마다 실행
  public void releaseMusics() {
    long updateCount = musicRepository.updatePendingToReleasedByBeforeNow(
        ReleaseStatus.PENDING,
        ReleaseStatus.RELEASED
    );
    log.info("PENDING -> RELEASED Update {} queries", updateCount);
  }

  @Transactional
  public MusicUploadResponse uploadMusic(
      MusicUploadRequest request,
      MultipartFile musicFile,
      @Nullable MultipartFile lyricFile
  ) {
    Album album = getAlbum(request.getAlbumId());
    AudioFileInfo audioFileInfo = uploadTrack(musicFile, lyricFile);

    Music music = createMusic(request, album, audioFileInfo);
    musicRepository.save(music);

    final FileKeys filekeys = audioFileInfo.getFileKeys();

    uploadLyricsIfExist(lyricFile, music, filekeys);

    return MusicUploadResponse.from(
        music,
        filekeys.getHighQualityKey(),
        filekeys.getLyricsKey()
    );
  }

  public AudioFileInfo uploadTrack(MultipartFile musicFile, MultipartFile lyricFile) {
    businessValidator.validateTrackFile(musicFile, lyricFile); // 검증
    return getAudioFileInfo(musicFile, lyricFile); // FileKeys and Duration
  }

  private AudioFileInfo getAudioFileInfo(
      MultipartFile musicFile,
      MultipartFile lyricsFile
  ) {
    try {
      TrackUpload trackUpload = createTrackUpload(musicFile, lyricsFile);
      FileKeys fileKeys = fileStorage.uploadTrack(trackUpload);

      return AudioFileInfo.of(fileKeys, trackUpload.getDuration());
    } catch (IOException e) {
      log.error("오디오 파일 프로세싱 실패. {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경 필요
    }
  }

  private TrackUpload createTrackUpload(
      MultipartFile musicFile,
      MultipartFile lyricsFile
  ) throws IOException {
    Map<AudioQuality, File> convertedMusicFiles = audioQualityConverter.process(musicFile);

    int duration = audioQualityConverter.extractDuration(
        convertedMusicFiles.get(AudioQuality.HIGH)
    );

    MusicUpload musicUpload = MusicUpload.of(musicFile, duration, convertedMusicFiles);
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
      Album album,
      AudioFileInfo audioFileInfo
  ) {
    FileKeys fileKeys = audioFileInfo.getFileKeys();
    return Music.builder()
        .album(album)
        .highQualityFileKey(fileKeys.getHighQualityKey())
        .mediumQualityFileKey(fileKeys.getMediumQualityKey())
        .lowQualityFileKey(fileKeys.getLowQualityKey())
        .title(request.getTitle())
        .trackNumber(request.getTrackNumber())
        .duration(audioFileInfo.getDuration())
        .releaseAt(request.getReleaseAt())
        .genre(request.getGenre())
        .titleTrack(request.getTitleTrack())
        .releaseStatus(ReleaseStatus.PENDING)
        .build();
  }

  private void uploadLyricsIfExist(MultipartFile lyricFile, Music music, FileKeys filekeys) {
    if (BusinessValidator.hasLyricsFile(lyricFile)) {
      Lyrics lyrics = createLyrics(lyricFile, music, filekeys.getLyricsKey());
      lyricsRepository.save(lyrics);
    }
  }

  private static Lyrics createLyrics(
      MultipartFile lyricFile,
      Music music,
      String lyricsKey
  ) {
    return Lyrics.builder()
        .music(music)
        .lyricsFileKey(lyricsKey)
        .lyricsFormat(LyricsMimeType.getFormatByContentType(lyricFile.getContentType()))
        .build();
  }
}
