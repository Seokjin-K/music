package com.music.util;

import com.music.constants.SortDirection;
import com.music.constants.SortType;
import java.util.Map;
import java.util.function.Function;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

public class SortUtils {

  public static final Map<SortType, Function<SortOrder, SortBuilder<?>>> SORT_MAP = Map.of(
      SortType.POPULARITY, order -> SortBuilders.fieldSort("play_count").order(order),
      SortType.LATEST, order -> SortBuilders.fieldSort("updated_at").order(order),
      SortType.RELEVANCE, order -> SortBuilders.scoreSort().order(order)
  );

  public static SortBuilder<?> getSortBuilder(SortType sortType, SortDirection direction) {
    return SORT_MAP.getOrDefault(
        sortType,
        order -> SortBuilders.scoreSort().order(order) // 기본 정렬은 관련성 기준
    ).apply(direction.toElasticsearchSortOrder());
  }
}
