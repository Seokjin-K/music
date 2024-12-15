package com.music.validator;

import com.music.eneity.constants.ReleaseStatus;
import java.util.EnumSet;
import org.springframework.web.multipart.MultipartFile;

public class BusinessValidator {

  public static boolean hasLyricsFile(MultipartFile lyricFile) {
    return lyricFile != null && !lyricFile.isEmpty();
  }

  public static void validateReleaseStatus(ReleaseStatus status) {
    if (!EnumSet.of(ReleaseStatus.PENDING, ReleaseStatus.RELEASED).contains(status)) {
      throw new RuntimeException(); // TODO: CustomException 으로 변경 필요
    }
  }
}
