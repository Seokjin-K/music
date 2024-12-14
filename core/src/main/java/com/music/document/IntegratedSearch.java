package com.music.document;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "integrated_search")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntegratedSearch {

  @Id
  private String id;

  // 정확한 매칭을 위한 키워드 타입
  // 문서 타입 구분에 필요 (ARTIST, ALBUM, MUSIC)
  @Field(type = FieldType.Keyword)
  private String type;

  // 분석기가 적용되어 토큰화됨
  // analyzer = "korean": 한글 분석 사용. 한글 검색의 정확도를 높이기 위함.
  @Field(type = FieldType.Text, analyzer = "korean")
  private String name; // 아티스트명, 앨범명, 음원명

  @Field(type = FieldType.Text, analyzer = "korean")
  private String content; // 설명, 가사 등 검색 대상이 되는 추가 텍스트

  @Field(type = FieldType.Date)
  private LocalDateTime createdAt;
}
