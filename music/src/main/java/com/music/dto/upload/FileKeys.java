package com.music.dto.upload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileKeys {

  private final String highQualityKey;
  private final String mediumQualityKey;
  private final String lowQualityKey;
  private final String lyricsKey;
}
