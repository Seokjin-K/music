package com.music.dto.upload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AudioFileInfo {

  private final FileKeys fileKeys;
  private final int duration;

  public static AudioFileInfo of(FileKeys fileKeys, int duration) {
    return AudioFileInfo.builder()
        .fileKeys(fileKeys)
        .duration(duration)
        .build();
  }
}
