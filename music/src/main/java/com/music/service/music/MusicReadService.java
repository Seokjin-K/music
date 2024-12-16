package com.music.service.music;

import com.music.dto.music.MusicReadResponse;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicReadService {

  private final MusicRepository musicRepository;

  @Transactional(readOnly = true)
  public MusicReadResponse getMusic(Long musicId) {
    return musicRepository.findByIdAndReleaseStatusWithAlbumAndArtist(
            musicId,
            ReleaseStatus.RELEASED
        )
        .map(MusicReadResponse::from)
        .orElseThrow(() -> {
          log.error("음원이 존재하지 않거나, RELEASE 상태가 아닙니다. musicId : {}", musicId);
          return new RuntimeException(); // TODO: CustomException 으로 변경
        });
  }
}
