package com.music.dto.music;

import com.music.constants.Genre;
import com.music.constants.ReleaseStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MusicUpdateRequest {

  private String title;

  // 현재 상태가 PENDING 이고 발매일이 오늘보다 이전이라면 해당 음원 상태 변경
  private LocalDate releaseAt;

  private Genre genre;

  private Boolean titleTrack;

  private ReleaseStatus releaseStatus; // 현재 상태와 동일한지 검사
}
