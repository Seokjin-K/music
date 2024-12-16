package com.music.constatns;

import com.music.eneity.constants.LyricsFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LyricsMimeType {

  LRC(LyricsFormat.LRC, "application/x-lrc", "lrc", 1024 * 100L), // 100KB
  TEXT(LyricsFormat.TEXT, "text/plain", "txt", 1024 * 30L); // 30KB

  private final LyricsFormat format;
  private final String mimeType;
  private final String extension;
  private final Long maxSize;

  // key: mimeType, value: LyricsContentType
  private static final Map<String, LyricsMimeType> MIME_TYPE_MAP =
      Arrays.stream(values()).collect(Collectors.toMap(LyricsMimeType::getMimeType, t -> t));

  public static boolean isSupportedLyricsType(String mimeType) {
    return MIME_TYPE_MAP.containsKey(mimeType);
  }

  public static Long getMaxSize(String mimeType) {
    LyricsMimeType lyricsContentType = MIME_TYPE_MAP.getOrDefault(mimeType, null);
    return lyricsContentType != null ? lyricsContentType.getMaxSize() : 0L;
  }

  public static LyricsFormat getFormatByContentType(String mimeType) {
    return Optional.ofNullable(MIME_TYPE_MAP.get(mimeType).getFormat())
        .orElseThrow(RuntimeException::new);
  }

  public static boolean isValidMimeTypeForExtension(String mimeType, String extension) {
    return MIME_TYPE_MAP.get(mimeType).getExtension().equals(extension);
  }
}
