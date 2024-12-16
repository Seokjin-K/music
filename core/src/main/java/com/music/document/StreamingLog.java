package com.music.document;

import java.time.LocalDateTime;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
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

  private LocalDateTime startTime;

  @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60) // 365일 후 자동 삭제
  private LocalDateTime endTime;

  private Integer totalDuration;
  private Integer playedDuration;
  private Double playedRatio;

  public void updateEndInfo(LocalDateTime endTime, Integer playedDuration) {
    this.endTime = endTime;
    this.playedDuration = playedDuration;
    calculatePlayRatio();
  }

  public void calculatePlayRatio() {
    if (this.totalDuration != null && this.playedDuration != null) {
      this.playedRatio = (double) this.playedDuration / this.totalDuration * 100;
    }
  }
}
