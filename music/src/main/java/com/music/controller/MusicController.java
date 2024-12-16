package com.music.controller;

import com.music.dto.music.MusicReadResponse;
import com.music.dto.music.MusicUpdateRequest;
import com.music.dto.music.MusicUpdateResponse;
import com.music.dto.music.MusicUploadResponse;
import com.music.dto.music.MusicUploadRequest;
import com.music.service.music.MusicReadService;
import com.music.service.music.MusicUpdateService;
import com.music.service.music.MusicUploadService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class MusicController {

  private final MusicUploadService musicUploadService;
  private final MusicReadService musicReadService;
  private final MusicUpdateService musicUpdateService;

  @PostMapping("/upload")
  public ResponseEntity<MusicUploadResponse> uploadMusic(
      @Valid @RequestPart MusicUploadRequest request,
      @RequestPart MultipartFile musicFile,
      @RequestPart(required = false) MultipartFile lyricFile) {
    return ResponseEntity.ok(musicUploadService.uploadMusic(request, musicFile, lyricFile));
  }

  @GetMapping("/{musicId}")
  public ResponseEntity<MusicReadResponse> getMusic(@PathVariable Long musicId) {
    return ResponseEntity.ok(musicReadService.getMusic(musicId));
  }

  @PutMapping("/update/{musicId}")
  public ResponseEntity<MusicUpdateResponse> updateMusic(
      @PathVariable Long musicId,
      @RequestPart MusicUpdateRequest request,
      @RequestPart(required = false) MultipartFile musicFile,
      @RequestPart(required = false) MultipartFile lyricsFile
  ) {
    return ResponseEntity.ok(
        musicUpdateService.updateMusic(musicId, request, musicFile, lyricsFile)
    );
  }
}
