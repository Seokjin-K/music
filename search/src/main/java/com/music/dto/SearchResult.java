package com.music.dto;

import com.music.document.IntegratedSearch;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResult {

  private String id;
  private String type;
  private String name;
  private String content;
  private LocalDateTime createdAt;

  public static SearchResult of(IntegratedSearch document, String highlightedName) {
    return SearchResult.builder()
        .id(document.getId())
        .type(document.getType())
        .name(document.getName())
        .content(document.getContent())
        .createdAt(document.getCreatedAt())
        .build();
  }
}
