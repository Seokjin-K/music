package com.music.logger.dto;

import com.music.document.StreamingLog;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingEndResponse {

  private String id;
  private Long musicId;
  private String sessionId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Integer totalDuration;
  private Integer playedDuration;
  private Double playedRatio;
  private LocalDateTime createdAt;
  // TODO : Add Expire After Day

  public static StreamingEndResponse from(StreamingLog streamingLog) {
    return StreamingEndResponse.builder()
        .id(streamingLog.getId())
        .musicId(streamingLog.getMusicId())
        .sessionId(streamingLog.getSessionId())
        .startTime(streamingLog.getStartTime())
        .endTime(streamingLog.getEndTime())
        .totalDuration(streamingLog.getTotalDuration())
        .playedDuration(streamingLog.getPlayedDuration())
        .playedRatio(streamingLog.getPlayedRatio())
        .build();
  }
}
