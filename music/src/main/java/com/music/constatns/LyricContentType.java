package com.music.constatns;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LyricContentType {

  LRC("application/x-lrc", "lrc", 1024 * 100L), // 100KB
  TEXT("text/plain", "txt", 1024 * 30L); // 30KB

  private final String mimeType;
  private final String extension;
  private final Long maxSize;

  // 현재 지원하는 타입만 설정
  private static final Set<LyricContentType> SUPPORTED_TYPES = EnumSet.of(LRC, TEXT);

  // key: mimeType, value: LyricsContentType Enum
  private static final Map<String, LyricContentType> MIME_TYPE_MAP =
      Arrays.stream(values())
          .collect(Collectors.toMap(LyricContentType::getMimeType, t -> t));

  public static boolean isSupportedLyricsType(String mimeType) {
    return Optional.ofNullable(MIME_TYPE_MAP.get(mimeType))
        .map(SUPPORTED_TYPES::contains)
        .orElse(false);
  }

  public static Long getMaxSize(String mimeType) {
    return Optional.ofNullable(MIME_TYPE_MAP.get(mimeType))
        .map(LyricContentType::getMaxSize)
        .orElse(0L);
  }
}
