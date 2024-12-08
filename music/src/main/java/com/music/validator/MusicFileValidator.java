package com.music.validator;

import com.music.constatns.AudioContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class MusicFileValidator implements FileValidator {

  public void validate(MultipartFile file) {
    String contentType = file.getContentType();
    if (!AudioContentType.isSupportedAudioType(contentType)) { // 지원하는 오디오 타입인지 검증
      log.error("지원하는 오디오 파일 형식이 아닙니다. {}", contentType);
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    if (file.getSize() > AudioContentType.getMaxSize(contentType)) { // 지원하는 파일 크기인지 검증
      log.error("파일 크기가 너무 큽니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
