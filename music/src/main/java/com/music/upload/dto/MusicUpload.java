package com.music.upload.dto;

import com.music.constatns.AudioQuality;
import com.music.constatns.FileType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class MusicUpload {

  private final Map<AudioQuality, File> fileByAudioQuality;
  private final String directory;
  private final String contentType;
  private final String originalFilename;

  public static MusicUpload of(
      MultipartFile musicFile, Map<AudioQuality, File> convertedMusicFiles) {

    return MusicUpload.builder()
        .fileByAudioQuality(convertedMusicFiles)
        .directory(FileType.MUSIC.getDirectory())
        .contentType(musicFile.getContentType())
        .originalFilename(musicFile.getOriginalFilename())
        .build();
  }

  public List<File> getFiles() {
    return new ArrayList<>(fileByAudioQuality.values());
  }
}
