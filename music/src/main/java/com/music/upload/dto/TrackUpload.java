package com.music.upload.dto;

import java.io.File;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@Builder
public class TrackUpload {

  private final MusicUpload musicUpload;

  @Nullable
  private final LyricsUpload lyricsUpload;

  public static TrackUpload of(
      MusicUpload musicUpload, LyricsUpload lyricsUpload) {

    return TrackUpload.builder()
        .musicUpload(musicUpload)
        .lyricsUpload(lyricsUpload)
        .build();
  }

  public Optional<LyricsUpload> getLyricsUploadOptional() {
    return Optional.ofNullable(lyricsUpload);
  }

  public List<File> getFiles() {
    List<File> files = this.musicUpload.getFiles();
    this.getLyricsUploadOptional().ifPresent(lyrics -> files.add(lyrics.getLyricsFile()));
    System.out.println("\n\n------------------------- 가사 파일 추가 완료 -------------------------\n\n");
    return files;
  }
}
