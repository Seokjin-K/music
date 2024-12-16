package com.music.dto.upload;

import com.music.eneity.Music;
import com.music.eneity.constants.Genre;
import com.music.eneity.constants.ReleaseStatus;
import java.time.LocalDate;
import javax.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MusicUploadResponse {

  private Long id;
  private Long albumId;
  private String highQualityFileKey;
  private String mediumQualityFileKey;
  private String lowQualityFileKey;
  private String lyricsFileKey;
  private String title;
  private Integer trackNumber;
  private Integer duration;
  private LocalDate releaseAt;
  private Genre genre;
  private Boolean titleTrack;
  private ReleaseStatus releaseStatus;

  public static MusicUploadResponse from(Music music, FileKeys fileKeys) {
    return MusicUploadResponse.builder()
        .id(music.getId())
        .albumId(music.getAlbum().getId())
        .highQualityFileKey(fileKeys.getHighQualityKey())
        .mediumQualityFileKey(fileKeys.getMediumQualityKey())
        .lowQualityFileKey(fileKeys.getLowQualityKey())
        .lyricsFileKey(fileKeys.getLyricsKey())
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
