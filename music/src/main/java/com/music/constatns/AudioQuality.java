package com.music.constatns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AudioQuality {

  HIGH("libmp3lame", 320000, 2, 44100), // 44.1kHz
  MEDIUM("libmp3lame", 192000, 2, 44100),
  LOW("libmp3lame", 128000, 2, 44100);

  private final String codec;
  private final int bitrate; // kbps : 1초당 1000비트를 보낼 수 있는 전송 속도
  private final int channels;
  private final int sampleRate;

  public static AudioQuality from(String networkQuality) {
    try {
      return networkQuality == null ? LOW :
          AudioQuality.valueOf(networkQuality.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return LOW;
    }
  }
}
