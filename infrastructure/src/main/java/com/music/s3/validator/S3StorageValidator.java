package com.music.s3.validator;

import com.music.constatns.AudioQuality;
import com.music.dto.upload.LyricsUpload;
import com.music.dto.upload.MusicUpload;
import com.music.dto.upload.TrackUpload;
import com.music.s3.FileValidator;
import java.io.File;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class S3StorageValidator {

  private final FileValidator fileValidator;

  public void validateTrackUpload(TrackUpload trackUpload) {
    MusicUpload musicUpload = trackUpload.getMusicUpload();
    validateMusicUpload(musicUpload);
    
    trackUpload.getLyricsUploadOptional()
        .ifPresent(lyrics -> {
          fileValidator.validateFileWithMetadata(
              lyrics.getLyricsFile(),
              lyrics.getContentType(),
              lyrics.getOriginalFilename()
          );
        });
  }

  public void validateMusicUpload(MusicUpload musicUpload) {
    for (Map.Entry<AudioQuality, File> entry : musicUpload.getFileByAudioQuality().entrySet()) {
      fileValidator.validateFileExists(entry.getValue());
    }
    fileValidator.validateContentType(musicUpload.getContentType());
    fileValidator.validateFileName(musicUpload.getOriginalFilename());
  }

  public void validateLyricsUpload(LyricsUpload lyricsUpload){
    fileValidator.validateFileWithMetadata(
        lyricsUpload.getLyricsFile(),
        lyricsUpload.getContentType(),
        lyricsUpload.getOriginalFilename()
    );
  }
}
