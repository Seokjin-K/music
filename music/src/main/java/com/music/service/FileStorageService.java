package com.music.service;

import com.music.constatns.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  String uploadFile(MultipartFile file, FileType fileType);
}
