package com.music.controller;

import com.music.dto.MusicResponse;
import com.music.dto.MusicUploadRequest;
import com.music.service.MusicService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class MusicController {

  private final MusicService musicService;

  @PostMapping("/upload")
  public ResponseEntity<MusicResponse> musicUpload(
      @Valid @RequestPart MusicUploadRequest request,
      @RequestPart MultipartFile musicFile,
      @RequestPart(required = false) MultipartFile lyricFile) {
    return ResponseEntity.ok(musicService.musicUpload(request, musicFile, lyricFile));
  }
}
