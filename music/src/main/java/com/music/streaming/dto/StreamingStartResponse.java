package com.music.streaming.dto;

import com.music.eneity.StreamingLog;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingStartResponse {

  private final Long musicId;
  private final String sessionId;
  private final Integer duration;
  private final LocalDateTime startTime;

  public static StreamingStartResponse from(StreamingLog streamingLog) {
    return StreamingStartResponse.builder()
        .musicId(streamingLog.getMusicId())
        .sessionId(streamingLog.getSessionId())
        .duration(streamingLog.getDuration())
        .startTime(streamingLog.getStartTime())
        .build();
  }
}
