package com.music.logger.service;

import com.music.document.StreamingLog;
import com.music.logger.dto.StreamingEndResponse;
import com.music.repository.StreamingLogRepository;
import com.music.logger.dto.StreamingEndRequest;
import com.music.logger.dto.StreamingStartRequest;
import com.music.logger.dto.StreamingStartResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StreamingLoggerService {

  private final StreamingLogRepository streamingLogRepository;

  public StreamingStartResponse logStart(StreamingStartRequest request) {
    String sessionId = UUID.randomUUID().toString();

    LocalDateTime now = LocalDateTime.now();

    StreamingLog streamingLog = StreamingLog.builder()
        .musicId(request.getMusicId())
        .sessionId(sessionId)
        .startTime(now)
        .totalDuration(request.getTotalDuration())
        .build();

    try {
      streamingLogRepository.save(streamingLog);
      log.info("로그 기록 시작 성공 - sessionId : {}", sessionId);
    } catch (Exception e) {
      log.info("로그 시작 실패 - sessionId : {}", sessionId);
    }
    return StreamingStartResponse.from(streamingLog);
  }

  public StreamingEndResponse logEnd(StreamingEndRequest request) {
    try {
      StreamingLog streamingLog = streamingLogRepository.findBySessionId(request.getSessionId())
          .orElseThrow(RuntimeException::new);

      streamingLog.updateEndInfo(LocalDateTime.now(), request.getPlayedDuration());
      streamingLogRepository.save(streamingLog);

      log.info("로그 기록 마침 성공 - sessionId : {}", request.getSessionId());
      return StreamingEndResponse.from(streamingLog);
    } catch (Exception e) {
      log.info("로그 종료 실패 - sessionId : {}", request.getSessionId());
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }
}
