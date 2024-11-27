package com.music.service;

import com.music.constatns.FileType;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {

  String uploadFile(MultipartFile file, FileType fileType);

  InputStream getFileStream(String musicFileKey);
}
