package com.music.chart.service;

import com.music.chart.dto.ChartResponse;
import com.music.constants.ChartType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicChartCacheService {

  private final MusicChartCache musicChartCache;

  public void saveMusicChartCache(List<ChartResponse> chartResponses, ChartType chartType) {
    musicChartCache.saveMusicChart(chartResponses, chartType);
  }

  public List<ChartResponse> getMusicChartCache(ChartType chartType, Pageable pageable) {
    int start = pageable.getPageNumber() * pageable.getPageSize() + 1;
    int end = Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), 100);
    
    if (start > 100){
      log.error("순위 제공은 100위까지만 가능합니다.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
    
    return musicChartCache.getMusicChart(chartType, start, end);
  }

  public Long getTotalCount(ChartType chartType) {
    return musicChartCache.getTotalCount(chartType);
  }
}
