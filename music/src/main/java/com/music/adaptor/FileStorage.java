package com.music.adaptor;

import com.music.dto.upload.FileKeys;
import com.music.dto.upload.TrackUpload;
import java.io.InputStream;

public interface FileStorage {

  FileKeys uploadTrack(TrackUpload filesUploadRequest);

  InputStream getFileStream(String musicFileKey);
}
