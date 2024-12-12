package com.music.chart.util;

import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MusicChartValidator {

  public static void validateMusic(Music music){
    if (music == null){
      log.error("Music not found Exception.");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }

    if (!ReleaseStatus.RELEASED.equals(music.getReleaseStatus())){
      log.error("Music is not released. musicId");
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
