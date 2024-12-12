package com.music.chart.period;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class MonthlyChartPeriodCalculator implements ChartPeriodCalculator {

  @Override
  public ChartPeriod calculate(LocalDateTime now) {
    LocalDateTime startTime = now
        .withDayOfMonth(1) // 해당 월의 1일로 설정
        .withHour(0)       // 0시
        .withMinute(0)     // 0분
        .withSecond(0);    // 0초

    LocalDateTime endTime = startTime
        .plusMonths(1)    // 다음 달
        .minusSeconds(1); // 1초를 빼서 이번 달의 마지막 시각으로 설정

    return ChartPeriod.builder()
        .startTime(startTime)
        .endTime(endTime)
        .build();
  }
}
