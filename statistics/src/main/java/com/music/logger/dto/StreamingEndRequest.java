package com.music.logger.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingEndRequest {

  private final String sessionId;
  private final Integer playedDuration;
}
