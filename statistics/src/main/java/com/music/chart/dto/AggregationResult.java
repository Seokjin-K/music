package com.music.chart.dto;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
public class AggregationResult {

  // MongoDB 에서 $group 연산을 수행할 때는 그룹화 기준이 되는 필드가 자동으로 '_id' 로 지정됨
  @Field("_id")
  private Long musicId;
  private Long playCount;
}
