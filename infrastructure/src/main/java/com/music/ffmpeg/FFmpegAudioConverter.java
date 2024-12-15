package com.music.ffmpeg;

import com.music.constatns.AudioQuality;
import com.music.adaptor.AudioConverter;
import com.music.util.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FFmpegAudioConverter implements AudioConverter {

  private static final String EXTENSION = ".mp3";
  private final FFmpegExecutor executor;

  @Override
  public File convert(File file, AudioQuality audioQuality) {
    // FFmpeg 는 파일 시스템 기반으로 동작하기 때문에
    // 입력과 출력 모두 실제 파일이 필요하다.
    File inputFile = null;
    File outputFile = null;

    try {
      inputFile = file;
      outputFile = FileUtils.createTempFile(EXTENSION);

      log.info("outputFile : {}", outputFile.getAbsolutePath());

      // FFmpeg 명령어 빌드
      FFmpegBuilder builder = new FFmpegBuilder()
          .setInput(inputFile.getAbsolutePath())             // 변환할 파일 경로
          .addOutput(outputFile.getAbsolutePath())           // 변환된 파일이 저장될 경로
          .setAudioCodec(audioQuality.getCodec())            // 오디오 코덱 (mp3 변환)
          .setAudioBitRate(audioQuality.getBitrate())        // 비트레이트 (음질 변환)
          .setAudioChannels(audioQuality.getChannels())      // 채널 수
          .setAudioSampleRate(audioQuality.getSampleRate())  // 샘플레이트
          .done();

      // 이 작업은 CPU 를 많이 사용하는 무거운 작업
      // 대용량 파일의 경우 상당한 시간이 소요될 수 있음
      executor.createJob(builder).run(); // 실행

      return outputFile;

    } catch (Exception e) {
      cleanupTempFiles(inputFile, outputFile);
      log.error("오디오 파일 변환 실패. {}", e.getMessage());
      throw new RuntimeException();
      // TODO: CustomException 으로 변경
    }
  }

  @Override
  public int extractAudioDuration(File file) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(
          "ffprobe",
          "-v", "quiet",
          "-show_entries", "format=duration",
          "-of", "csv=p=0",
          file.getAbsolutePath()
      );
      Process process = processBuilder.start();

      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream()))
      ) {
        String result = reader.readLine();

        log.info("Duration : {}", result);
        return (int) Double.parseDouble(result);
      }
    } catch (Exception e) {
      log.error("Duration 추출 실패 : {}", e.getMessage());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  private void cleanupTempFiles(File... files) {
    for (File file : files) {
      if (file != null && file.exists()) {
        FileUtils.deleteFile(file);
      }
    }
  }
}
