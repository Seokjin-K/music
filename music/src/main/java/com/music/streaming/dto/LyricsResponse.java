package com.music.streaming.dto;

import com.music.eneity.constants.LyricsFormat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LyricsResponse {

  private LyricsFormat format;
  private String content;
}
