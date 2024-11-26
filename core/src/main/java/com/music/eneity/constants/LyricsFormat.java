package com.music.eneity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LyricsFormat {
  LRC("lrc"),
  TEXT("txt");

  private final String description;
}
