package com.music.dto.upload;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileKeys {

  private final String highQualityKey;
  private final String mediumQualityKey;
  private final String lowQualityKey;
  private final String lyricsKey;

  public static FileKeys of(String highQualityKey, String mediumQualityKey, String lowQualityKey) {
    return FileKeys.builder()
        .highQualityKey(highQualityKey)
        .mediumQualityKey(mediumQualityKey)
        .lowQualityKey(lowQualityKey)
        .build();
  }

  public String[] getExistFileKeys() {
    return Stream.of(highQualityKey, mediumQualityKey, lowQualityKey, lyricsKey)
        .filter(Objects::nonNull)
        .toArray(String[]::new);
  }
}
