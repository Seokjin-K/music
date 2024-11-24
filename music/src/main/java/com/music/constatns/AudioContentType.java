package com.music.constatns;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AudioContentType {
  // AAC 5분 곡 기준 ~10MB로 예상
  AAC("audio/aac", "aac", 1024 * 1024 * 20L), // 20MB
  // mp3 10분 곡 기준 ~25MB로 예상
  MP3("audio/mpeg", "mp3", 1024 * 1024 * 30L), // 30MB
  // flac 10분 곡 기준 ~40MB로 예상
  FLAC("audio/flac", "flac", 1024 * 1024 * 50L); // 50MB

  private final String mimeType;
  private final String extension;
  private final Long maxSize;

  // 현재 지원하는 타입만 설정
  private static final Set<AudioContentType> SUPPORTED_TYPES = EnumSet.of(AAC, MP3, FLAC);

  // key: mimeType, value: AudioContentType Enum
  private static final Map<String, AudioContentType> MIME_TYPE_MAP =
      Arrays.stream(values())
          .collect(Collectors.toMap(AudioContentType::getMimeType, t -> t));

  public static boolean isSupportedAudioType(String mimeType) {
    return MIME_TYPE_MAP.containsKey(mimeType);
  }

  public static Long getMaxSize(String mimeType) {
    AudioContentType audioContentType = MIME_TYPE_MAP.getOrDefault(mimeType, null);
    return audioContentType != null ? audioContentType.getMaxSize() : 0L;
  }
}
