package com.music.chart.dto;

import com.music.eneity.Music;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChartResponse {

  private Long musicId;
  private Integer chartRank;
  private String title;
  private String artistName;
  private Long playCount;
  private String coverFileKey;
  private Integer duration;

  public static ChartResponse of(Music music, Integer chartRank, Long playCount) {
    return ChartResponse.builder()
        .musicId(music.getId())
        .chartRank(chartRank)
        .title(music.getTitle())
        .artistName(music.getArtistName())
        .playCount(playCount)
        .coverFileKey(music.getCoverFileKey())
        .duration(music.getDuration())
        .build();
  }
}
