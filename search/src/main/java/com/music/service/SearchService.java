package com.music.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.constants.SortDirection;
import com.music.constants.SortType;
import com.music.document.IntegratedSearch;
import com.music.dto.IntegratedSearchResponse;
import com.music.util.SortUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

  private final RestHighLevelClient client;
  private final ObjectMapper objectMapper;

  public IntegratedSearchResponse search(
      String keyword,
      SortType sortType,
      SortDirection sortDirection,
      Pageable pageable
  ) {
    try {
      // 검색 요청을 위한 컨테이너
      SearchRequest searchRequest = new SearchRequest("integrated_search");
      // 검색 쿼리 구성을 위한 빌더
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      BoolQueryBuilder boolQuery = createBoolQueryBuilder(keyword);

      configurePaging(pageable, searchSourceBuilder, boolQuery); // 페이징 처리
      searchSourceBuilder.sort(SortUtils.getSortBuilder(sortType, sortDirection)); // 정렬 설정
      searchRequest.source(searchSourceBuilder);

      // 검색 실행
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      List<IntegratedSearch> documents = convertSearchHits(searchResponse.getHits());

      long totalCount = Optional.ofNullable(searchResponse.getHits().getTotalHits())
          .map(totalHits -> totalHits.value)
          .orElse(0L);

      return IntegratedSearchResponse.of(documents, totalCount, pageable);

    } catch (IOException e) {
      log.error("검색 중 오류 발생: {}", e.getMessage());
      throw new ElasticsearchException("검색 중 오류 발생", e);
    }
  }

  private static void configurePaging(Pageable pageable, SearchSourceBuilder searchSourceBuilder,
      BoolQueryBuilder boolQuery) {
    searchSourceBuilder.query(boolQuery)
        .from(pageable.getPageNumber() * pageable.getPageSize())
        .size(pageable.getPageSize());
  }

  private static BoolQueryBuilder createBoolQueryBuilder(String keyword) {
    return QueryBuilders.boolQuery()
        // 정확한 매칭 쿼리
        .should(QueryBuilders.termQuery("music_title.keyword", keyword).boost(3.0f))
        .should(QueryBuilders.termQuery("artist_name.keyword", keyword).boost(2.5f))
        .should(QueryBuilders.termQuery("album_title.keyword", keyword).boost(2.5f))

        // 부분 매칭 쿼리
        .should(QueryBuilders.matchQuery("music_title", keyword).boost(3.0f))
        .should(QueryBuilders.matchQuery("artist_name", keyword).boost(2.5f))
        .should(QueryBuilders.matchQuery("artist_description", keyword))
        .should(QueryBuilders.matchQuery("album_title", keyword).boost(2.5f))
        .should(QueryBuilders.matchQuery("album_description", keyword))
        // 최소 하나의 조건은 만족해야 함
        .minimumShouldMatch(1);
  }

  // 검색 결과 변환 메소드
  private List<IntegratedSearch> convertSearchHits(SearchHits hits) {
    return Arrays.stream(hits.getHits())
        // JSON to Document. Elasticsearch 는 JSON 형태로 데이터를 저장/반환.
        .map(hit -> objectMapper.convertValue(
            hit.getSourceAsMap(),
            IntegratedSearch.class
        ))
        .collect(Collectors.toList());
  }
}
