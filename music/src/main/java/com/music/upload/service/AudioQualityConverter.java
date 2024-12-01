package com.music.upload.service;

import com.music.adaptor.AudioConverter;
import com.music.constatns.AudioQuality;
import com.music.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class AudioQualityConverter {

  private final AudioConverter audioConverter;

  public Map<AudioQuality, File> process(MultipartFile originalFile) throws IOException {
    Map<AudioQuality, File> fileByAudioQuality = new EnumMap<>(AudioQuality.class);
    createInitialFiles(originalFile, fileByAudioQuality);
    convertFiles(audioConverter, fileByAudioQuality);
    return fileByAudioQuality;
  }

  private void createInitialFiles(
      MultipartFile originalFile, Map<AudioQuality, File> fileByAudioQuality) throws IOException {

    File highQualityFile = FileUtils.convertMultipartFileToFile(originalFile);
    fileByAudioQuality.put(AudioQuality.HIGH, highQualityFile);

    String extension = "." + FileUtils.getExtension(originalFile.getOriginalFilename());

    for (AudioQuality quality : EnumSet.of(AudioQuality.MEDIUM, AudioQuality.LOW)) {
      File copiedFile = FileUtils.fileCopy(highQualityFile, extension);
      fileByAudioQuality.put(quality, copiedFile);
    }
  }

  private void convertFiles(
      AudioConverter audioConverter, Map<AudioQuality, File> fileByAudioQuality) {

    for (AudioQuality quality : EnumSet.of(AudioQuality.MEDIUM, AudioQuality.LOW)) {
      fileByAudioQuality.compute(quality, (k, file) -> audioConverter.convert(file, quality));
      log.info("{} 품질 파일 변환 완료", quality);
    }
  }
}
