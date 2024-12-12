package com.music.logger.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StreamingStartRequest {

  private final Long musicId;
  private final Integer totalDuration;
}
