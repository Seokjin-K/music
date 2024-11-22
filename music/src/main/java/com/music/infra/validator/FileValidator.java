package com.music.infra.validator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

  void validate(MultipartFile file);
}
