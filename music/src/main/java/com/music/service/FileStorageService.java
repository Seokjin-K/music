package com.music.service;

import com.music.constatns.FileType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileStorageService {

  String uploadFile(MultipartFile file, FileType fileType);
}
