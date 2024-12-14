package com.music.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.constants.SortDirection;
import com.music.constants.SortType;
import com.music.document.IntegratedSearch;
import com.music.dto.SearchResult;
import com.music.dto.IntegratedSearchResponse;
import com.music.util.SortUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
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

      // Bool 쿼리 생성 - name, content 필드 검색
      BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
          // 이름 필드에 가중치 부여
          // 음원 관련
          .should(QueryBuilders.matchQuery("musicTitle", keyword).boost(3.0f))
          .should(QueryBuilders.matchQuery("lyrics", keyword))
          // 아티스트 관련
          .should(QueryBuilders.matchQuery("artistName", keyword).boost(2.5f))
          .should(QueryBuilders.matchQuery("artistDescription", keyword))
          // 앨범 관련
          .should(QueryBuilders.matchQuery("albumTitle", keyword).boost(2.5f))
          .should(QueryBuilders.matchQuery("albumDescription", keyword))
          // 공통
          .should(QueryBuilders.matchQuery("genre", keyword))
          .minimumShouldMatch(1); // 최소 하나의 조건은 만족해야 함

      // 페이징 처리
      searchSourceBuilder.query(boolQuery)
          .from(pageable.getPageNumber() * pageable.getPageSize())
          .size(pageable.getPageSize());

      // 정렬 설정
      searchSourceBuilder.sort(SortUtils.getSortBuilder(sortType, sortDirection));
      searchRequest.source(searchSourceBuilder);

      // 검색 실행
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      List<SearchResult> documents = convertSearchHits(searchResponse.getHits());

      long totalCount = Optional.ofNullable(searchResponse.getHits().getTotalHits())
          .map(totalHits -> totalHits.value)
          .orElse(0L);

      // 결과 변환 및 반환
      return IntegratedSearchResponse.of(documents, totalCount, pageable);

    } catch (IOException e) {
      log.error("검색 중 오류 발생: {}", e.getMessage());
      throw new ElasticsearchException("검색 중 오류 발생", e);
    }
  }

  // 검색 결과 변환 메소드
  private List<SearchResult> convertSearchHits(SearchHits hits) {
    return Arrays.stream(hits.getHits())
        .map(hit -> {
          // JSON to Document. Elasticsearch 는 JSON 형태로 데이터를 저장/반환.
          IntegratedSearch document = objectMapper.convertValue(
              hit.getSourceAsMap(),
              IntegratedSearch.class
          );

          // 하이라이팅 처리
          Map<String, HighlightField> highlightFields = hit.getHighlightFields();
          String highlightedName = null;
          if (highlightFields.containsKey("name")) {
            highlightedName = highlightFields.get("name").getFragments()[0].string();
          }
          return SearchResult.of(document, highlightedName);
        })
        .collect(Collectors.toList());
  }
}
