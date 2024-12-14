package com.music.chart.period;

import com.music.constants.ChartType;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ChartPeriodFactory {

  private final Map<ChartType, ChartPeriodCalculator> calculators;

  public ChartPeriodFactory(
      HourlyChartPeriodCalculator hourlyCalculator,
      DailyChartPeriodCalculator dailyCalculator,
      WeeklyChartPeriodCalculator weeklyCalculator,
      MonthlyChartPeriodCalculator monthlyCalculator
  ) {
    calculators = Map.of(
        ChartType.HOURLY, hourlyCalculator,
        ChartType.DAILY, dailyCalculator,
        ChartType.WEEKLY, weeklyCalculator,
        ChartType.MONTHLY, monthlyCalculator
    );
  }

  public ChartPeriod getChartPeriod(ChartType type, LocalDateTime now) {
    return calculators.get(type).calculate(now);
  }
}
