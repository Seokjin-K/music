package com.music.constants;

import org.elasticsearch.search.sort.SortOrder;

public enum SortDirection {
  ASC,
  DESC;

  public SortOrder toElasticsearchSortOrder() {
    return this == SortDirection.ASC ? SortOrder.ASC : SortOrder.DESC;
  }
}
