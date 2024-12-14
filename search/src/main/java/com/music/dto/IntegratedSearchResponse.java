package com.music.dto;

import com.music.document.IntegratedSearch;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@Builder
public class IntegratedSearchResponse {

  private List<IntegratedSearch> documents;
  private long totalCount;
  private int currentPage;
  private int pageSize;

  public static IntegratedSearchResponse of(
      List<IntegratedSearch> documents,
      long totalCount,
      Pageable pageable
  ) {
    return IntegratedSearchResponse.builder()
        .documents(documents)
        .totalCount(totalCount)
        .currentPage(pageable.getPageNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }
}
