package com.music.eneity;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "streaming_logs")
@Getter
@Builder
public class StreamingLog {

  @Id
  private String id; // MongoDB 에서는 id를 String 으로 표현

  private Long musicId;
  private String sessionId;

  @CreatedDate
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  private Integer duration;
  private Integer playedDuration;
  private Double playedRatio;

  @Indexed(expireAfterSeconds = 30 * 24 * 60 * 60) // 30일 후 자동 삭제
  private LocalDateTime createdAt;

  public void updateEndInfo(LocalDateTime endTime, Integer playedDuration) {
    this.endTime = endTime;
    this.playedDuration = playedDuration;
    calculatePlayRatio();
  }

  public void calculatePlayRatio() {
    if (duration != null && playedDuration != null) {
      this.playedRatio = (double) playedDuration / duration * 100;
    }
  }
}
