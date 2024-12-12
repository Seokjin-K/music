package com.music.chart.util;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.music.chart.period.ChartPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChartAggregationBuilder {

  public static final String AGGREGATION_FILTERING_DATE = "endTime";
  public static final String AGGREGATION_FILTERING_RATIO = "playRatio";
  public static final String AGGREGATION_GROUP_CRITERIA = "musicId";
  public static final String AGGREGATION_COUNT_CRITERIA = "playCount";
  public static final int AGGREGATION_PLAY_RATIO = 50;
  public static final int AGGREGATION_LIMIT = 100;

  public Aggregation buildMusicChartAggregation(ChartPeriod chartPeriod) {
    return Aggregation.newAggregation(
        match( // 기간으로 필터링
            where(AGGREGATION_FILTERING_DATE)
                .gte(chartPeriod.getStartTime())
                .lte(chartPeriod.getEndTime())
        ),
        match( // 재생 비율 필터링
            Criteria.where(AGGREGATION_FILTERING_RATIO)
                .gte(AGGREGATION_PLAY_RATIO)
        ),
        group(AGGREGATION_GROUP_CRITERIA) // musicId로 그룹
            .count().as(AGGREGATION_COUNT_CRITERIA), // 재생 횟수 집계
        sort(Direction.DESC, AGGREGATION_COUNT_CRITERIA),
        limit(AGGREGATION_LIMIT)
    );
  }
}
