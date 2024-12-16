package com.music.dto.music;

import com.music.constants.Genre;
import com.music.projection.MusicResponseProjection;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicReadResponse {

  private String title;
  private String artistName;
  private Integer duration;
  private LocalDate releaseAt;
  private Genre genre;
  private Boolean titleTrack;

  public static MusicReadResponse from(MusicResponseProjection projection) {
    return MusicReadResponse.builder()
        .title(projection.getTitle())
        .artistName(projection.getArtistName())
        .duration(projection.getDuration())
        .releaseAt(projection.getReleaseAt())
        .genre(projection.getGenre())
        .titleTrack(projection.getTitleTrack())
        .build();
  }
}
