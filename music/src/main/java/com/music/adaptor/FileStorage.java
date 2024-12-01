package com.music.adaptor;

import com.music.upload.dto.FileKeys;
import com.music.upload.dto.TrackUpload;
import java.io.InputStream;

public interface FileStorage {

  FileKeys uploadTrack(TrackUpload filesUploadRequest);

  InputStream getFileStream(String musicFileKey);
}
