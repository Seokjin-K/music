package com.music.chart.period;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DailyChartPeriodCalculator implements ChartPeriodCalculator {

  @Override
  public ChartPeriod calculate(LocalDateTime now) {
    LocalDateTime startTime = now
        .withHour(0)    // 0시
        .withMinute(0)  // 0분
        .withSecond(0); // 0초
    
    LocalDateTime endTime = startTime
        .plusDays(1)      // 다음 날
        .minusSeconds(1); // 1초를 빼서 오늘의 마지막 시각으로 설정

    return ChartPeriod.builder()
        .startTime(startTime)
        .endTime(endTime)
        .build();
  }
}
