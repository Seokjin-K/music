package com.music.document;

import java.time.LocalDateTime;
import javax.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
  private String musicId;

  @Field(name = "music_title", type = FieldType.Text)
  private String musicTitle;

  @Field(type = FieldType.Long)
  private Long playCount;

  @Field(type = FieldType.Text)
  private String artistName;

  @Field(type = FieldType.Text)
  private String artistDescription;

  @Field(type = FieldType.Text)
  private String albumTitle;

  @Field(type = FieldType.Text)
  private String albumDescription;

  @Field(type = FieldType.Date)
  private LocalDateTime updatedAt;
}
