package com.music.chart.period;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class HourlyChartPeriodCalculator implements ChartPeriodCalculator {

  @Override
  public ChartPeriod calculate(LocalDateTime now) {
    LocalDateTime startTime = now
        .withMinute(0)  // 0분
        .withSecond(0); // 0초

    LocalDateTime endTime = startTime
        .plusHours(1)     // 1시간 후
        .minusSeconds(1); // 1초를 빼서 해당 시간의 마지막 시각으로 설정

    return ChartPeriod.builder()
        .startTime(startTime)
        .endTime(endTime)
        .build();
  }
}
