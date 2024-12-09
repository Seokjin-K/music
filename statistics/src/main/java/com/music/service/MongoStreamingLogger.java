package com.music.service;

import com.music.adaptor.StreamingLogger;
import com.music.eneity.StreamingLog;
import com.music.repository.StreamingLogRepository;
import com.music.streaming.dto.StreamingEndRequest;
import com.music.streaming.dto.StreamingStartRequest;
import com.music.streaming.dto.StreamingStartResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MongoStreamingLogger implements StreamingLogger {

  private final StreamingLogRepository streamingLogRepository;

  @Override
  public StreamingStartResponse logStart(StreamingStartRequest request) {
    String sessionId = UUID.randomUUID().toString();

    StreamingLog streamingLog = StreamingLog.builder()
        .musicId(request.getMusicId())
        .sessionId(sessionId)
        .duration(request.getDuration())
        .startTime(LocalDateTime.now())
        .build();

    try {
      streamingLogRepository.save(streamingLog);

      log.info("로그 기록 시작 - sessionId : {}", sessionId);
    } catch (Exception e) {
      log.info("로그 시작 실패 - sessionId : {}", sessionId);
    }
    return StreamingStartResponse.from(streamingLog);
  }

  @Override
  public void logEnd(StreamingEndRequest request) {
    try {
      StreamingLog streamingLog = streamingLogRepository.findBySessionId(request.getSessionId())
          .orElseThrow(RuntimeException::new);

      streamingLog.updateEndInfo(LocalDateTime.now(), request.getPlayedDuration());
      streamingLogRepository.save(streamingLog);

      log.info("로그 기록 종료 - sessionId : {}", request.getSessionId());
    } catch (Exception e) {
      log.info("로그 종료 실패 - sessionId : {}", request.getSessionId());
    }
  }
}
