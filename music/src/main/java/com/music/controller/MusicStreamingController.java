package com.music.controller;

import com.music.adaptor.StreamingLogger;
import com.music.eneity.StreamingLog;
import com.music.streaming.dto.LyricsResponse;
import com.music.streaming.dto.StreamResponse;
import com.music.streaming.dto.StreamingEndRequest;
import com.music.streaming.dto.StreamingStartRequest;
import com.music.streaming.dto.StreamingStartResponse;
import com.music.streaming.service.MusicStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/streaming")
@RequiredArgsConstructor
public class MusicStreamingController {

  private final MusicStreamingService musicStreamingService;
  private final StreamingLogger streamingLogger;

  @GetMapping("/{musicId}")
  public ResponseEntity<Resource> streamMusic(
      @PathVariable Long musicId,
      @RequestHeader("Stream-Quality") String quality) {

    StreamResponse response = musicStreamingService.musicStreaming(musicId, quality);
    return ResponseEntity.ok()
        .headers(response.getHeaders())
        .body(response.getResource());
  }

  @PostMapping("/log-start")
  public ResponseEntity<StreamingStartResponse> startStreamingLog(
      @RequestBody StreamingStartRequest request) {
    return ResponseEntity.ok(streamingLogger.logStart(request));
  }

  @PostMapping("/log-end")
  public ResponseEntity<Void> endStreamingLog(@RequestBody StreamingEndRequest request) {
    streamingLogger.logEnd(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{musicId}/lyrics")
  public ResponseEntity<LyricsResponse> getLyrics(@PathVariable Long musicId) {
    return ResponseEntity.ok(musicStreamingService.getLyrics(musicId));
  }
}
