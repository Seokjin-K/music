package com.music.controller;

import com.music.dto.streaming.LyricsResponse;
import com.music.dto.streaming.StreamResponse;
import com.music.service.streaming.MusicStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
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
  public ResponseEntity<Resource> streamMusic(
      @PathVariable Long musicId,
      @RequestHeader("Stream-Quality") String quality) {

    StreamResponse response = musicStreamingService.musicStreaming(musicId, quality);
    return ResponseEntity.ok()
        .headers(response.getHeaders())
        .body(response.getResource());
  }

  @GetMapping("/{musicId}/lyrics")
  public ResponseEntity<LyricsResponse> getLyrics(@PathVariable Long musicId) {
    return ResponseEntity.ok(musicStreamingService.getLyrics(musicId));
  }
}
