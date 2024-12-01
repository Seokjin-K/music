package com.music.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.ObjectMetadataProvider;
import com.music.constatns.AudioQuality;
import com.music.upload.dto.MusicUpload;
import com.music.upload.dto.LyricsUpload;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomObjectMetadataProvider implements ObjectMetadataProvider {

  private final MusicUpload musicUpload;
  private final LyricsUpload lyricsUpload;
  private final Iterator<Entry<AudioQuality, File>> musicIterator;
  private boolean isLyricsProcessed;

  public CustomObjectMetadataProvider(MusicUpload musicUpload, LyricsUpload lyricsUpload) {
    this.musicUpload = musicUpload;
    this.lyricsUpload = lyricsUpload;
    this.musicIterator = musicUpload.getFileByAudioQuality().entrySet().iterator();
    this.isLyricsProcessed = false;
  }

  @Override
  public void provideObjectMetadata(File file, ObjectMetadata metadata) {
    if (musicIterator.hasNext()) {
      applyMusicMetadata(musicIterator.next(), metadata);
      log.info(
          "\napplyMusicMetadata for file {}: Content-Type: {}, Content-Length: {}, User Metadata: {}\n",
          file.getName(), metadata.getContentType(), metadata.getContentLength(), metadata.getUserMetadata());
    } else if (lyricsUpload != null && !isLyricsProcessed) {
      applyLyricsMetadata(file, metadata);
      log.info("\napplyLyricsMetadata : {}\n", metadata);
      log.info(
          "\napplyLyricsMetadata for file {}: Content-Type: {}, Content-Length: {}, User Metadata: {}\n",
          file.getName(), metadata.getContentType(), metadata.getContentLength(), metadata.getUserMetadata());
      isLyricsProcessed = true;
    } else {
      log.warn("\nUnexpected file encountered: {}\n", file.getName());
    }
  }

  private void applyMusicMetadata(Map.Entry<AudioQuality, File> entry, ObjectMetadata metadata) {
    metadata.setContentLength(entry.getValue().length());
    metadata.setContentType(musicUpload.getContentType());
    metadata.addUserMetadata("directory", musicUpload.getDirectory());
    metadata.addUserMetadata("quality", entry.getKey().toString());
    metadata.addUserMetadata("originalFilename", musicUpload.getOriginalFilename());
  }

  private void applyLyricsMetadata(File file, ObjectMetadata metadata) {
    metadata.setContentLength(file.length());
    metadata.setContentType(lyricsUpload.getContentType());
    metadata.addUserMetadata("directory", lyricsUpload.getDirectory());
    metadata.addUserMetadata("originalFilename", lyricsUpload.getOriginalFilename());
  }
}
