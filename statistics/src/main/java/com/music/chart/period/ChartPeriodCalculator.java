package com.music.chart.period;

import java.time.LocalDateTime;

public interface ChartPeriodCalculator {

  ChartPeriod calculate(LocalDateTime now);
}
