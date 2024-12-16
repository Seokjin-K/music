package com.music.logger.dto;

import com.music.document.StreamingLog;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingStartResponse {

  private final Long musicId;
  private final String sessionId;
  private final Integer totalDuration;
  private final LocalDateTime startTime;

  public static StreamingStartResponse from(StreamingLog streamingLog) {
    return StreamingStartResponse.builder()
        .musicId(streamingLog.getMusicId())
        .sessionId(streamingLog.getSessionId())
        .totalDuration(streamingLog.getTotalDuration())
        .startTime(streamingLog.getStartTime())
        .build();
  }
}
