package com.music.validator;

import com.music.constatns.LyricsMimeType;
import com.music.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class LyricsFileValidator implements FileValidator {

  public void validate(MultipartFile file) {
    String contentType = file.getContentType();
    if (!LyricsMimeType.isSupportedLyricsType(contentType)) { // 지원하는 가사 타입인지 검증
      log.error("지원하는 가사 파일 형식이 아닙니다. {}", contentType);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    if (file.getSize() > LyricsMimeType.getMaxSize(contentType)) { // 지원하는 파일 크기 인지 검증
      log.error("파일 크기가 너무 크거나 지원하는 유형이 아닙니다. {}", contentType);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    String extension = FileUtils.getExtension(file.getOriginalFilename());
    if (!LyricsMimeType.isValidMimeTypeForExtension(contentType, extension)) {
      throw new RuntimeException("Mime Type 과 확장자가 다릅니다."); // TODO: CustomException 으로 변경
    }
  }
}
