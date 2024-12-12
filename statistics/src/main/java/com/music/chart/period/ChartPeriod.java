package com.music.chart.period;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChartPeriod {

  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
}
