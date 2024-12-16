package com.music.service.music;

import com.music.constants.ReleaseStatus;
import com.music.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicDeleteService {

  private final MusicRepository musicRepository;

  @Transactional
  public Long deleteMusic(Long musicId) {
    int deleteCount = musicRepository.updateMusicStatus(musicId, ReleaseStatus.DELETED);
    return deleteCount > 0 ? musicId : -1;
  }
}
