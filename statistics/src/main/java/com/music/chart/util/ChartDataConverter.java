package com.music.chart.util;

import com.music.chart.dto.AggregationResult;
import java.util.ArrayList;
import java.util.List;

public class ChartDataConverter {

  public static List<Long> getMusicIdsFromAggregationResults(
      List<AggregationResult> aggregationResults) {

    List<Long> musicIds = new ArrayList<>();
    for (AggregationResult stat : aggregationResults) {
      musicIds.add(stat.getMusicId());
    }
    return musicIds;
  }
}
