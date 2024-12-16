package com.music.dto.upload;

import com.music.constatns.FileType;
import java.io.File;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class LyricsUpload {

  private final File lyricsFile;
  private final String directory;
  private final String contentType;
  private final String originalFilename;

  public static LyricsUpload of(MultipartFile lyricsFile, File convertedLyricsFile) {

    return LyricsUpload.builder()
        .lyricsFile(convertedLyricsFile)
        .directory(FileType.LYRICS.getDirectory())
        .contentType(lyricsFile.getContentType())
        .originalFilename(lyricsFile.getOriginalFilename())
        .build();
  }
}
