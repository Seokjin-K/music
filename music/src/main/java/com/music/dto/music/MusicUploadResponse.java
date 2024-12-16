<<<<<<<< HEAD:music/src/main/java/com/music/dto/music/MusicUploadResponse.java
package com.music.dto.music;
========
package com.music.dto.upload;
>>>>>>>> feature/chart:music/src/main/java/com/music/dto/upload/MusicResponse.java

import com.music.dto.upload.FileKeys;
import com.music.eneity.Music;
import com.music.constants.Genre;
import com.music.constants.ReleaseStatus;
import java.time.LocalDate;
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
