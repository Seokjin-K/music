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
public class MusicService {

  private final AlbumRepository albumRepository;
  private final MusicRepository musicRepository;
  private final FileStorageService fileStorageService;

  public MusicResponse musicUpload(
      MusicUploadRequest request, MultipartFile musicFile, MultipartFile lyricFile) {

    Album album = validateAlbum(request.getAlbumId()); // 유효한 앨범인지 검증
    validateFile(musicFile, FileType.MUSIC); // 음원 파일 검증
    String musicFileKey = fileStorageService.uploadFile(musicFile, FileType.MUSIC); // 음원 업로드

    String lyricFilekey = null;
    if (lyricFile != null && !lyricFile.isEmpty()) {
      validateFile(lyricFile, FileType.LYRIC);
      lyricFilekey = fileStorageService.uploadFile(lyricFile, FileType.LYRIC); // 가사 업로드
    }

    Music music = createMusic(request, album, musicFileKey, lyricFilekey);
    musicRepository.save(music);

    // TODO: 발매일 오후6에 'RELEASED' 상태 되도록 스케줄링 등록 필요

    return MusicResponse.from(music);
  }

  private void validateFile(MultipartFile file, FileType fileType) {
    fileType.getFileValidator().validate(file); // 타입에 따라 다른 구현체의 validate 메서드 실행
  }

  private Music createMusic(
      MusicUploadRequest request, Album album, String musicFileKey, String lyricFileKey) {

    return Music.builder()
        .album(album)
        .musicFileKey(musicFileKey)
        .lyricFileKey(lyricFileKey)
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
