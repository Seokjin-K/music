package com.music.s3;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileValidator {

  public void validateFileWithMetadata(File file, String contentType, String originalFilename){
    validateFileExists(file);
    validateContentType(contentType);
    validateFileName(originalFilename);
  }

  public void validateFileExists(File file) {
    if (file == null) {
      log.error("파일이 null입니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    if (!file.exists()) {
      log.error("파일이 존재하지 않습니다: {}", file.getAbsolutePath());
      throw new RuntimeException();  // TODO: CustomException 으로 변경
    }
    if (!file.isFile()) {
      log.error("유효한 파일이 아닙니다: {}", file.getAbsolutePath());
      throw new RuntimeException();  // TODO: CustomException 으로 변경
    }
    if (!file.canRead()) {
      log.error("파일을 읽을 수 없습니다: {}", file.getAbsolutePath());
      throw new RuntimeException();  // TODO: CustomException 으로 변경
    }
  }

  public void validateContentType(String contentType) {
    if (contentType == null || contentType.trim().isEmpty()) {
      log.error("파일 형식을 확인할 수 없습니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }

  public void validateFileName(String filename) {
    if (filename == null || filename.trim().isEmpty()) {
      log.error("파일명이 유효하지 않습니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
