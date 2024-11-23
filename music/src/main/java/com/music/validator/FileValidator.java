package com.music.validator;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

  void validate(MultipartFile file);
}
