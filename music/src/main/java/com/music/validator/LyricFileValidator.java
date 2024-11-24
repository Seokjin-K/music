package com.music.validator;

import com.music.constatns.LyricContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class LyricFileValidator implements FileValidator {

  @Override
  public void validate(MultipartFile file) {
    String contentType = file.getContentType();
    if (!LyricContentType.isSupportedLyricsType(contentType)) { // 지원하는 가사 타입인지 검증
      log.error("지원하는 가사 파일 형식이 아닙니다. {}", contentType);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    if (file.getSize() > LyricContentType.getMaxSize(contentType)) { // 지원하는 파일 크기 인지 검증
      log.error("파일 크기가 너무 크거나 지원하는 유형이 아닙니다. {}", contentType);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
