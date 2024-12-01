package com.music.validator;

import com.music.constatns.FileType;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidatorFactory {

  private final Map<FileType, FileValidator> validators;

  public FileValidatorFactory(
      MusicFileValidator musicValidator,
      LyricsFileValidator lyricsValidator) {

    validators = Map.of(
        FileType.MUSIC, musicValidator,
        FileType.LYRICS, lyricsValidator
    );
  }

  public void getValidator(MultipartFile multipartFile, FileType type) {
    validators.get(type).validate(multipartFile);
  }
}
