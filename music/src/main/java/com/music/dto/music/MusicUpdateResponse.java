package com.music.dto.music;

import com.music.constants.Genre;
import com.music.constants.ReleaseStatus;
import com.music.eneity.Lyrics;
import com.music.eneity.Music;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@Builder
public class MusicUpdateResponse {

  private Long musicId;

  private Long lyricsId;

  private String highQualityFileKey;

  private String mediumQualityFileKey;

  private String lowQualityFileKey;

  private String title;

  private Integer trackNumber;

  private Integer duration;

  private LocalDate releaseAt;

  private Genre genre;

  private Boolean titleTrack;

  private ReleaseStatus releaseStatus;

  public static MusicUpdateResponse of(Music music, @Nullable Lyrics lyrics) {
    return MusicUpdateResponse.builder()
        .musicId(music.getId())
        .lyricsId(lyrics != null ? lyrics.getId() : null)
        .highQualityFileKey(music.getHighQualityFileKey())
        .mediumQualityFileKey(music.getMediumQualityFileKey())
        .lowQualityFileKey(music.getLowQualityFileKey())
        .title(music.getTitle())
        .trackNumber(music.getTrackNumber())
        .duration(music.getDuration())
        .releaseAt(music.getReleaseAt())
        .genre(music.getGenre())
        .titleTrack(music.getTitleTrack())
        .releaseStatus(music.getReleaseStatus())
        .build();
  }
}
