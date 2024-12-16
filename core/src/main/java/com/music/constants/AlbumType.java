package com.music.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlbumType {
  SINGLE("싱글"),
  EP("미니"),
  FULL("정규"),
  REPACKAGE("리패키지"),
  SPECIAL("스페셜");

  private final String description;
}
