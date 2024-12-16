package com.music.controller;

import com.music.chart.service.MusicChartService;
import com.music.chart.dto.ChartResponse;
import com.music.dto.PageResponse;
import com.music.constants.ChartType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chart")
@RequiredArgsConstructor
public class ChartController {

  private final MusicChartService chartService;

  @GetMapping
  public ResponseEntity<PageResponse<ChartResponse>> getChart(
      @RequestParam(defaultValue = "DAILY") ChartType chartType,
      @PageableDefault Pageable pageable) {

    Page<ChartResponse> chartResponsePage = chartService.getMusicChart(chartType, pageable);
    return ResponseEntity.ok(new PageResponse<>(chartResponsePage));
  }
}
