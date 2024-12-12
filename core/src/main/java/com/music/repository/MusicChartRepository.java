package com.music.repository;

import com.music.eneity.MusicChart;
import com.music.eneity.constants.ChartType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicChartRepository extends JpaRepository<MusicChart, Long> {

  @Query("SELECT mc FROM MusicChart mc " +
      "WHERE mc.music.id IN :musicIds " +
      "AND mc.endTime = (" +
      "    SELECT MAX(m.endTime) " +
      "    FROM MusicChart m " +
      "    WHERE m.music.id = mc.music.id AND m.chartType = :chartType" +
      ")")
  List<MusicChart> findPreviousChartsByMusicIds(List<Long> musicIds, ChartType chartType);
}
