package com.music.controller;

import com.music.constants.SortDirection;
import com.music.constants.SortType;
import com.music.dto.CustomSearchResponse;
import com.music.dto.IntegratedSearchResponse;
import com.music.service.SearchService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @GetMapping
  public ResponseEntity<IntegratedSearchResponse> search(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "POPULARITY") SortType sortType,
      @RequestParam(defaultValue = "DESC") SortDirection sortDirection,
      @PageableDefault Pageable pageable
  ) {
    return ResponseEntity.ok(searchService.search(keyword, sortType, sortDirection, pageable));
  }
}
