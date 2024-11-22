package com.music.service;

import com.music.constatns.FileType;
import com.music.dto.MusicResponse;
import com.music.dto.MusicUploadRequest;
import com.music.eneity.Album;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.AlbumRepository;
import com.music.repository.MusicRepository;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Primary // 구현체가 여러 개일 때 해당 클래스 우선 주입
@Transactional
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

  private final AlbumRepository albumRepository;
  private final MusicRepository musicRepository;
  private final FileStorageService fileStorageService;

  @Override
  public MusicResponse musicUpload(
      MusicUploadRequest request, MultipartFile musicFile, MultipartFile lyricFile) {

    Album album = validateAlbum(request.getAlbumId()); // 유효한 앨범인지 검증
    String musicFileKey = fileStorageService.uploadFile(musicFile, FileType.MUSIC); // 음원 업로드

    String lyricsFileUrl = null;
    if (lyricFile != null && lyricFile.isEmpty()) {
      lyricsFileUrl = fileStorageService.uploadFile(lyricFile, FileType.LYRICS); // 가사 업로드
    }

    Music music = createMusic(request, album, musicFileKey, lyricsFileUrl);
    musicRepository.save(music);

    // TODO: 발매일 오후6에 'RELEASED' 상태 되도록 스케줄링 등록 필요

    return MusicResponse.from(music);
  }

  private Music createMusic(
      MusicUploadRequest request, Album album, String musicFileKey, String lyricsFileKey) {

    return Music.builder()
        .album(album)
        .musicFileKey(musicFileKey)
        .lyricsFileKey(lyricsFileKey)
        .title(request.getTitle())
        .trackNumber(request.getTrackNumber())
        .duration(request.getDuration())
        .releaseAt(request.getReleaseAt())
        .genre(request.getGenre())
        .titleTrack(request.getTitleTrack())
        .releaseStatus(ReleaseStatus.PENDING)
        .build();
  }

  private Album validateAlbum(Long albumId) {
    Album album = albumRepository.findById(albumId)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경 필요

    if (!EnumSet.of(ReleaseStatus.PENDING, ReleaseStatus.RELEASED)
        .contains(album.getStatus())) {
      throw new RuntimeException(); // TODO: CustomException 으로 변경 필요
    }
    return album;
  }
}
