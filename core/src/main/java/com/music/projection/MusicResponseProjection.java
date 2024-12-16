package com.music.projection;

import com.music.constants.Genre;
import java.time.LocalDate;

public interface MusicResponseProjection {

  String getTitle();

  String getArtistName();

  Integer getDuration();

  LocalDate getReleaseAt();

  Genre getGenre();

  Boolean getTitleTrack();
}
