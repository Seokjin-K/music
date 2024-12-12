package com.music.chart.service;

import com.music.chart.dto.AggregationResult;
import com.music.chart.period.ChartPeriod;
import com.music.chart.util.ChartDataConverter;
import com.music.chart.util.MusicChartValidator;
import com.music.eneity.Music;
import com.music.eneity.MusicChart;
import com.music.eneity.constants.ChartType;
import com.music.repository.MusicChartRepository;
import com.music.repository.MusicRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicChartDBService {

  private final MusicRepository musicRepository;
  private final MusicChartRepository musicChartRepository;

  @Transactional
  public void saveMusicChart(
      List<AggregationResult> aggregationResults,
      ChartType chartType,
      ChartPeriod chartPeriod) {

    try {
      List<MusicChart> musicCharts = createMusicCharts(aggregationResults, chartType, chartPeriod);

      musicChartRepository.saveAll(musicCharts);
      log.info("새로운 순위 저장 완료");
    } catch (Exception e) {
      log.error("새로운 순위 저장 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  private List<MusicChart> createMusicCharts(
      List<AggregationResult> aggregationResults,
      ChartType chartType,
      ChartPeriod chartPeriod) {

    List<Long> musicIdsFromAggregationResults =
        ChartDataConverter.getMusicIdsFromAggregationResults(aggregationResults);

    Map<Long, Music> musicMap = createMusicIdByMusic(musicIdsFromAggregationResults);

    Map<Long, Integer> previousRanks =
        calculateRankChanges(musicIdsFromAggregationResults, chartType);

    List<MusicChart> musicCharts = new ArrayList<>();
    for (int i = 0; i < aggregationResults.size(); i++) {
      musicCharts.add(createMusicChart(
          aggregationResults.get(i),
          musicMap,
          previousRanks,
          chartType,
          chartPeriod,
          i + 1
      ));
    }
    return musicCharts;
  }

  private MusicChart createMusicChart(
      AggregationResult aggregationResult,
      Map<Long, Music> musicMap,
      Map<Long, Integer> previousRanks,
      ChartType chartType,
      ChartPeriod chartPeriod,
      int rank) {

    Long musicId = aggregationResult.getMusicId();
    Music music = musicMap.get(musicId);

    Integer previousRank = previousRanks.get(musicId);
    Integer rankChange = previousRank != null ? previousRank - rank : null;

    return MusicChart.builder()
        .music(music)
        .chartType(chartType)
        .startTime(chartPeriod.getStartTime())
        .endTime(chartPeriod.getEndTime())
        .chartRank(rank)
        .playCount(aggregationResult.getPlayCount())
        .rankChange(rankChange)
        .build();
  }

  @Transactional(readOnly = true)
  public Map<Long, Music> createMusicIdByMusic(List<Long> musicIds) {
    Map<Long, Music> musicMap = new HashMap<>();
    List<Music> musicList = musicRepository.findAllByIdWithAlbumAndArtist(musicIds);

    for (Music music : musicList) {
      MusicChartValidator.validateMusic(music);
      musicMap.put(music.getId(), music);
    }
    return musicMap;
  }

  @Transactional(readOnly = true)
  private Map<Long, Integer> calculateRankChanges(List<Long> musicIds, ChartType chartType) {
    List<MusicChart> previousCharts =
        musicChartRepository.findPreviousChartsByMusicIds(musicIds, chartType);

    Map<Long, Integer> rankChanges = new HashMap<>();
    for (MusicChart chart : previousCharts) {
      rankChanges.put(chart.getMusic().getId(), chart.getChartRank());
    }
    return rankChanges;
  }
}
