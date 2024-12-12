package com.music.chart.service;

import com.music.chart.dto.ChartResponse;
import com.music.eneity.constants.ChartType;
import java.util.List;

public interface MusicChartCache {

  void saveMusicChart(List<ChartResponse> chartResponses, ChartType chartType);
  List<ChartResponse> getMusicChart(ChartType chartType, int start, int end);
  Long getTotalCount(ChartType chartType);
}
