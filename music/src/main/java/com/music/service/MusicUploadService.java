package com.music.service;

import com.music.constatns.FileType;
import com.music.constatns.LyricsMimeType;
import com.music.dto.MusicResponse;
import com.music.dto.MusicUploadRequest;
import com.music.eneity.Album;
import com.music.eneity.Lyrics;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.AlbumRepository;
import com.music.repository.LyricsRepository;
import com.music.repository.MusicRepository;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MusicUploadService {

  private final AlbumRepository albumRepository;
  private final MusicRepository musicRepository;
  private final LyricsRepository lyricsRepository;
  private final FileStorageService fileStorageService;

  public MusicResponse musicUpload(
      MusicUploadRequest request, MultipartFile musicFile, MultipartFile lyricFile) {

    Album album = getAlbum(request.getAlbumId());
    validateFile(musicFile, FileType.MUSIC);

    String musicFileKey = fileStorageService.uploadFile(musicFile, FileType.MUSIC);
    Music music = createMusic(request, album, musicFileKey);
    musicRepository.save(music);

    if (hasLyricsFile(lyricFile)) {
      uploadLyrics(lyricFile, music);
    }

    // TODO: 발매일 오후6에 'RELEASED' 상태 되도록 스케줄링 등록 필요

    return MusicResponse.from(music);
  }

  private Album getAlbum(Long albumId) {
    Album album = albumRepository.findById(albumId)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경 필요

    if (!EnumSet.of(ReleaseStatus.PENDING, ReleaseStatus.RELEASED)
        .contains(album.getStatus())) {
      throw new RuntimeException(); // TODO: CustomException 으로 변경 필요
    }
    return album;
  }

  private void validateFile(MultipartFile file, FileType fileType) {
    fileType.getFileValidator().validate(file); // 타입에 따라 다른 구현체의 validate 메서드 실행
  }

  private Music createMusic(
      MusicUploadRequest request, Album album, String musicFileKey) {

    return Music.builder()
        .album(album)
        .musicFileKey(musicFileKey)
        .title(request.getTitle())
        .trackNumber(request.getTrackNumber())
        .duration(request.getDuration())
        .releaseAt(request.getReleaseAt())
        .genre(request.getGenre())
        .titleTrack(request.getTitleTrack())
        .releaseStatus(ReleaseStatus.PENDING)
        .build();
  }

  private static boolean hasLyricsFile(MultipartFile lyricFile) {
    return lyricFile != null && !lyricFile.isEmpty();
  }

  private void uploadLyrics(MultipartFile lyricFile, Music music) {
    validateFile(lyricFile, FileType.LYRIC);
    String lyricsFileKey = fileStorageService.uploadFile(lyricFile, FileType.LYRIC);

    log.info("\ncontentType: {}\n", lyricFile.getContentType());
    Lyrics build = Lyrics.builder()
        .music(music)
        .lyricsFileKey(lyricsFileKey)
        .format(LyricsMimeType.getFormatByContentType(lyricFile.getContentType()))
        .build();

    lyricsRepository.save(build);
  }
}
