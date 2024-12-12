package com.music.controller;

import com.music.logger.StreamingLogger;
import com.music.logger.dto.StreamingEndRequest;
import com.music.logger.dto.StreamingEndResponse;
import com.music.logger.dto.StreamingStartRequest;
import com.music.logger.dto.StreamingStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/log")
@RequiredArgsConstructor
public class LogController {

  private final StreamingLogger streamingLogger;

  @PostMapping("/start")
  public ResponseEntity<StreamingStartResponse> startStreamingLog(
      @RequestBody StreamingStartRequest request) {
    return ResponseEntity.ok(streamingLogger.logStart(request));
  }

  @PostMapping("/end")
  public ResponseEntity<StreamingEndResponse> endStreamingLog(
      @RequestBody StreamingEndRequest request) {
    return ResponseEntity.ok(streamingLogger.logEnd(request));
  }
}
