package com.music.chart.service;

import com.music.chart.dto.ChartResponse;
import com.music.chart.period.ChartPeriod;
import com.music.chart.period.ChartPeriodFactory;
import com.music.chart.util.ChartAggregationBuilder;
import com.music.chart.util.ChartDataConverter;
import com.music.chart.util.MusicChartValidator;
import com.music.eneity.Music;
import com.music.chart.dto.AggregationResult;
import com.music.eneity.StreamingLog;
import com.music.eneity.constants.ChartType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicChartService {

  private final MongoTemplate mongoTemplate;
  private final MusicChartCacheService chartCacheService;
  private final MusicChartDBService musicChartDBService;
  private final ChartAggregationBuilder chartAggregationBuilder;
  private final ChartPeriodFactory chartPeriodFactory;

  //@Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
  @Scheduled(cron = "0 0 * * * *") // 매 시간 정각에 실행
  private void updateAllCharts() {
    log.info("모든 차트 업데이트 실행");
    for (ChartType chartType : ChartType.values()) {
      updateMusicChart(chartType);
    }
    log.info("모든 차트 업데이트 완료");
  }

  public List<ChartResponse> updateMusicChart(ChartType chartType) {
    log.info("{} 차트 업데이트", chartType);

    ChartPeriod chartPeriod = chartPeriodFactory.getChartPeriod(chartType, LocalDateTime.now());
    List<AggregationResult> aggregationResults = streamingLogAggregation(chartPeriod);

    List<ChartResponse> chartResponses = createChartResponse(aggregationResults);

    chartCacheService.saveMusicChartCache(chartResponses, chartType); // Cache 저장
    musicChartDBService.saveMusicChart(aggregationResults, chartType, chartPeriod); // RDB 저장

    return chartResponses;
  }

  public Page<ChartResponse> getMusicChart(ChartType chartType, Pageable pageable) {
    log.debug("{} 순위 차트 가져오기", chartType);
    List<ChartResponse> chatResponses =
        chartCacheService.getMusicChartCache(chartType, pageable);

    if (chatResponses.isEmpty()) { // Cache miss
      return handleCacheMiss(chartType, pageable);
    }
    return new PageImpl<>(chatResponses, pageable, chartCacheService.getTotalCount(chartType));
  }

  private Page<ChartResponse> handleCacheMiss(ChartType chartType, Pageable pageable) {
    List<ChartResponse> chatResponses = updateMusicChart(chartType);
    return new PageImpl<>(chatResponses, pageable, chartCacheService.getTotalCount(chartType));
  }

  private List<AggregationResult> streamingLogAggregation(ChartPeriod chartPeriod) {
    log.info("Start Streaming-log Aggregation");
    return mongoTemplate.aggregate(
        chartAggregationBuilder.buildMusicChartAggregation(chartPeriod),
        StreamingLog.class,
        AggregationResult.class
    ).getMappedResults();
  }

  private List<ChartResponse> createChartResponse(List<AggregationResult> aggregationResults) {
    List<Long> musicIds =
        ChartDataConverter.getMusicIdsFromAggregationResults(aggregationResults);
    Map<Long, Music> musicMap = musicChartDBService.createMusicIdByMusic(musicIds);

    List<ChartResponse> chartResponses = new ArrayList<>();
    for (int i = 0; i < aggregationResults.size(); i++) {
      AggregationResult aggregationResult = aggregationResults.get(i);
      Music music = musicMap.get(aggregationResult.getMusicId());
      chartResponses.add(
          ChartResponse.of(music, i + 1, aggregationResult.getPlayCount())
      );
    }
    return chartResponses;
  }
}
