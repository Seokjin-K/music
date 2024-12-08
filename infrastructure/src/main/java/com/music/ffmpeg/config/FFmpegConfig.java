package com.music.ffmpeg.config;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FFmpegConfig {

  @Value("${ffmpeg.path}")
  private String ffmpegPath;

  @Bean
  public FFmpeg ffmpeg() {
    try {
      return new FFmpeg(ffmpegPath);
    } catch (IOException e) {
      log.error("FFmpeg 빈 생성 중에 오류 : {}", e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }

  @Bean
  public FFmpegExecutor ffmpegExecutor(FFmpeg ffmpeg) {
    try {
      return new FFmpegExecutor(ffmpeg);
    } catch (IOException e) {
      log.error("FFmpegExecutor 빈 생성 중에 오류 : {}", e.getMessage());
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
