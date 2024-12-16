package com.music.validator;

import com.music.constants.ReleaseStatus;
import com.music.constatns.FileType;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessValidator {

  private final FileValidatorFactory fileValidatorFactory;

  public void validateTrackFile(MultipartFile musicFile, MultipartFile lyricFile) {
    if (hasLyricsFile(lyricFile)) {
      fileValidatorFactory.getValidator(lyricFile, FileType.LYRICS);
    }
    fileValidatorFactory.getValidator(musicFile, FileType.MUSIC);
  }

  public void validateFile(MultipartFile file, FileType fileType) {
    fileValidatorFactory.getValidator(file, fileType);
  }

  public static boolean hasLyricsFile(MultipartFile lyricFile) {
    return lyricFile != null && !lyricFile.isEmpty();
  }

  public static void validateReleaseStatus(ReleaseStatus status) {
    if (!EnumSet.of(ReleaseStatus.PENDING, ReleaseStatus.RELEASED).contains(status)) {
      log.error("ReleaseStatus invalid : {}", status);
      throw new RuntimeException(); // TODO: CustomException 으로 변경 필요
    }
  }
}
