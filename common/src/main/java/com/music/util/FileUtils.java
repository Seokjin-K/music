package com.music.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtils {

  public static String getExtension(String filename) {
    if (filename == null) {
      return "";
    }
    int lastDotIndex = filename.lastIndexOf('.');
    return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
  }

  public static File createTempFile() throws IOException {
    return new File(System.getProperty("java.io.tmpdir"));
  }

  public static File createTempFile(String extension) throws IOException {
    // 자동으로 임시 디렉토리 사용
    return File.createTempFile(UUID.randomUUID().toString(), extension);
  }

  public static void deleteFile(File file) {
    if (file != null && file.exists() && !file.delete()) {
      log.warn("파일 삭제 실패 : {}", file.getAbsolutePath());
    }
  }

  public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
    String extension = "." + getExtension(multipartFile.getOriginalFilename());
    File convertedFile = createTempFile(extension);

    // MultipartFile 의 내용을 실제 파일 시스템의 File 로 전달
    multipartFile.transferTo(convertedFile);
    return convertedFile;
  }

  public static File fileCopy(File sourceFile, String extension) throws IOException {
    File targetFile = FileUtils.createTempFile(extension);
    Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    return targetFile;
  }
}
