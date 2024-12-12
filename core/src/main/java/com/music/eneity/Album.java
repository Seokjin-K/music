package com.music.eneity;

import com.music.eneity.constants.AlbumType;
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
public class Album extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "artist_id", nullable = false)
  private Artist artist;

  @Column(nullable = false)
  private String title;

  @Column
  private String coverFileKey;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Genre genre;

  @Column
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AlbumType type;

  @Column(nullable = false)
  private LocalDate releaseAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReleaseStatus status;

  public String getArtistName() {
    return this.artist.getName();
  }
}
