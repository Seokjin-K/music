package com.music.service;

import com.music.dto.MusicResponse;
import com.music.dto.MusicUploadRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MusicService {

  MusicResponse musicUpload(
      MusicUploadRequest request, MultipartFile musicFile, MultipartFile lyricFile);
}
