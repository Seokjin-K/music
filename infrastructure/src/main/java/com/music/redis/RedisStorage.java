package com.music.redis;

import com.music.chart.dto.ChartResponse;
import com.music.chart.service.MusicChartCache;
import com.music.eneity.constants.ChartType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStorage implements MusicChartCache {

  public static final String CHART_KEY_PREFIX = "chart:";
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  public void saveMusicChart(List<ChartResponse> chartResponses, ChartType chartType) {
    try {
      String chartKey = CHART_KEY_PREFIX + chartType.name();

      redisTemplate.delete(chartKey);

      ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
      chartResponses.forEach(chartResponse ->
          zSetOps.add(chartKey, chartResponse, chartResponse.getChartRank())
      );

      log.info("음원 차트 캐시 저장 완료");
    } catch (Exception e) {
      log.error("음원 차트 캐시 저장 실패");
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }

  @Override
  public List<ChartResponse> getMusicChart(ChartType chartType, int start, int end) {
    try {
      String chartKey = CHART_KEY_PREFIX + chartType.name();
      ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
      Set<Object> responses = zSetOps.reverseRange(chartKey, start - 1, end - 1);

      if (responses == null || responses.isEmpty()) {
        log.info("캐시 데이터 없음");
        return Collections.emptyList();
      }

      log.info("음원 차트 캐시 가져오기 완료");
      return responses.stream()
          .map(response -> (ChartResponse) response)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.info("음원 차트 캐시 가져오기 실패");
      throw new RuntimeException(e);
    }
  }

  @Override
  public Long getTotalCount(ChartType chartType) {
    String key = CHART_KEY_PREFIX + chartType.name();
    return redisTemplate.opsForZSet().size(key);
  }
}
