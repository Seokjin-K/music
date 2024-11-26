package com.music.controller;

import com.music.dto.StreamResponse;
import com.music.service.MusicStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/streaming")
@RequiredArgsConstructor
public class MusicStreamingController {

  private final MusicStreamingService musicStreamingService;

  @GetMapping("/{musicId}")
  public ResponseEntity<Resource> musicStream(
      @PathVariable Long musicId,
      @RequestHeader HttpHeaders headers) {
    StreamResponse response = musicStreamingService.musicStreaming(musicId, headers);
    return ResponseEntity.ok()
        .headers(response.getHeaders())
        .body(response.getResource());
  }

}
