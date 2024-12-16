package com.music.adaptor;

import com.music.dto.upload.FileKeys;
import com.music.dto.upload.LyricsUpload;
import com.music.dto.upload.MusicUpload;
import com.music.dto.upload.TrackUpload;
import com.music.eneity.Lyrics;
import java.io.InputStream;

public interface FileStorage {

  FileKeys uploadTrack(TrackUpload filesUploadRequest);

  FileKeys uploadMusic(MusicUpload musicUpload);
  String uploadLyrics(LyricsUpload lyricsUpload);

  InputStream getFileStream(String musicFileKey);

  void deleteFiles(FileKeys fileKeys);
  void deleteFile(String fileKey);
}
