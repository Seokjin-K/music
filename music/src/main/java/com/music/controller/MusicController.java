package com.music.controller;

import com.music.dto.music.MusicResponse;
import com.music.dto.upload.MusicUploadResponse;
import com.music.dto.upload.MusicUploadRequest;
import com.music.service.music.MusicService;
import com.music.service.upload.MusicUploadService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class MusicController {

  private final MusicUploadService musicUploadService;
  private final MusicService musicService;

  @PostMapping("/upload")
  public ResponseEntity<MusicUploadResponse> uploadMusic(
      @Valid @RequestPart MusicUploadRequest request,
      @RequestPart MultipartFile musicFile,
      @RequestPart(required = false) MultipartFile lyricFile) {
    return ResponseEntity.ok(musicUploadService.uploadMusic(request, musicFile, lyricFile));
  }

  @GetMapping("/{musicId}")
  public ResponseEntity<MusicResponse> getMusic(@PathVariable Long musicId) {
    return ResponseEntity.ok(musicService.getMusic(musicId));
  }
}