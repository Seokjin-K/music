package com.music.constatns;

import java.util.Arrays;
import java.util.Map;
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

  // key: mimeType, value: LyricsContentType Enum
  private static final Map<String, LyricContentType> MIME_TYPE_MAP =
      Arrays.stream(values())
          .collect(Collectors.toMap(LyricContentType::getMimeType, t -> t));

  public static boolean isSupportedLyricsType(String mimeType) {
    return MIME_TYPE_MAP.containsKey(mimeType);
  }

  public static Long getMaxSize(String mimeType) {
    LyricContentType lyricContentType = MIME_TYPE_MAP.getOrDefault(mimeType, null);
    return lyricContentType != null ? lyricContentType.getMaxSize() : 0L;
  }
}
