package com.music.dto.upload;

import com.music.constatns.AudioQuality;
import com.music.constatns.FileType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class MusicUpload {

  private final Map<AudioQuality, File> fileByAudioQuality;
  private final Integer duration;
  private final String directory;
  private final String contentType;
  private final String originalFilename;

  public static MusicUpload of(
      MultipartFile musicFile,
      Integer duration,
      Map<AudioQuality, File> convertedMusicFiles
  ) {
    return MusicUpload.builder()
        .fileByAudioQuality(convertedMusicFiles)
        .duration(duration)
        .directory(FileType.MUSIC.getDirectory())
        .contentType(musicFile.getContentType())
        .originalFilename(musicFile.getOriginalFilename())
        .build();
  }

  public List<File> getFiles() {
    return new ArrayList<>(fileByAudioQuality.values());
  }
}
