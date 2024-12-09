package com.music.upload.dto;

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
