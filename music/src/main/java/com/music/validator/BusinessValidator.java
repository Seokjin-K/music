package com.music.validator;

import com.music.constatns.FileType;
import com.music.eneity.constants.ReleaseStatus;
import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class BusinessValidator {

  private final FileValidatorFactory fileValidatorFactory;

  public void validateTrackFile(MultipartFile musicFile, MultipartFile lyricFile){
    if (hasLyricsFile(lyricFile)) {
      fileValidatorFactory.getValidator(lyricFile, FileType.LYRICS);
    }
    fileValidatorFactory.getValidator(musicFile, FileType.MUSIC);
  }

  public static boolean hasLyricsFile(MultipartFile lyricFile) {
    return lyricFile != null && !lyricFile.isEmpty();
  }

  public static void validateReleaseStatus(ReleaseStatus status) {
    if (!EnumSet.of(ReleaseStatus.PENDING, ReleaseStatus.RELEASED).contains(status)) {
      throw new RuntimeException(); // TODO: CustomException 으로 변경 필요
    }
  }
}
