package com.music.chart.period;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Component;

@Component
public class WeeklyChartPeriodCalculator implements ChartPeriodCalculator {

  @Override
  public ChartPeriod calculate(LocalDateTime now) {
    LocalDateTime startTime = now
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) // 이번 주 월요일로 설정
        .withHour(0)    // 0시
        .withMinute(0)  // 0분
        .withSecond(0); // 0초

    LocalDateTime endTime = startTime
        .plusWeeks(1)     // 다음 주
        .minusSeconds(1); // 1초를 빼서 이번 주의 마지막 시각으로 설정

    return ChartPeriod.builder()
        .startTime(startTime)
        .endTime(endTime)
        .build();
  }
}
