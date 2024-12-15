package com.music.dto.music;

import com.music.eneity.constants.Genre;
import com.music.repository.MusicResponseProjection;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicResponse {

  private String title;
  private String artistName;
  private Integer duration;
  private LocalDate releaseAt;
  private Genre genre;
  private Boolean titleTrack;

  public static MusicResponse from(MusicResponseProjection projection) {
    return MusicResponse.builder()
        .title(projection.getTitle())
        .artistName(projection.getArtistName())
        .duration(projection.getDuration())
        .releaseAt(projection.getReleaseAt())
        .genre(projection.getGenre())
        .titleTrack(projection.getTitleTrack())
        .build();
  }
}
