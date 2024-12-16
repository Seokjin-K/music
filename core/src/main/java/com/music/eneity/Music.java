package com.music.eneity;

import com.music.eneity.constants.Genre;
import com.music.eneity.constants.ReleaseStatus;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Music extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "album_id", nullable = false)
  private Album album;

  @Column(nullable = false)
  private String highQualityFileKey;

  @Column(nullable = false)
  private String mediumQualityFileKey;

  @Column(nullable = false)
  private String lowQualityFileKey;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false)
  private Integer trackNumber;

  @Column(nullable = false)
  private Integer duration;

  @Column(nullable = false)
  private LocalDate releaseAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @Column(nullable = false)
  private Boolean titleTrack;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReleaseStatus releaseStatus;

  public String getArtistName() {
    return this.album.getArtistName();
  }

  public String getCoverFileKey() {
    return this.getAlbum().getCoverFileKey();
  }

  public void update(
      String title,
      LocalDate releaseAt,
      Genre genre,
      Boolean titleTrack,
      ReleaseStatus releaseStatus
  ) {
    if (title != null) {
      this.title = title;
    }
    if (releaseAt != null) {
      this.releaseAt = releaseAt;
    }
    if (genre != null) {
      this.genre = genre;
    }
    if (titleTrack != null) {
      this.titleTrack = titleTrack;
    }
    if (releaseStatus != null) {
      this.releaseStatus = releaseStatus;
    }
  }

  public void updateAudioFileInfo(
      String highQualityFileKey,
      String mediumQualityFileKey,
      String lowQualityFileKey,
      Integer duration
  ) {
    this.highQualityFileKey = highQualityFileKey;
    this.mediumQualityFileKey = mediumQualityFileKey;
    this.lowQualityFileKey = lowQualityFileKey;
    this.duration = duration;
  }
}
