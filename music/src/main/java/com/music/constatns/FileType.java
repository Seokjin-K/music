package com.music.constatns;

import com.music.infra.validator.FileValidator;
import com.music.infra.validator.impl.LyricFileValidator;
import com.music.infra.validator.impl.MusicFileValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileType {
  MUSIC("music", new MusicFileValidator()),
  LYRICS("lyrics", new LyricFileValidator());

  private final String directory;
  private final FileValidator fileValidator;
}
