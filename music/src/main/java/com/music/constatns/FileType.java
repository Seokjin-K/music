package com.music.constatns;

import com.music.validator.FileValidator;
import com.music.validator.LyricFileValidator;
import com.music.validator.MusicFileValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum FileType {
  MUSIC("music", new MusicFileValidator()),
  LYRIC("lyric", new LyricFileValidator());

  private final String directory;
  private final FileValidator fileValidator;
}
