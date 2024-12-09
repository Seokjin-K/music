package com.music.streaming.dto;

import com.music.eneity.constants.LyricsFormat;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class LyricsResponse {

  private LyricsFormat format;

  @Builder.Default
  private String content = "가사 정보가 없습니다.";
}
