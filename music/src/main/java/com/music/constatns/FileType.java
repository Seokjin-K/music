package com.music.constatns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
  MUSIC("music"),
  LYRICS("lyrics"),
  TRACK("track");

  private final String directory;
}
