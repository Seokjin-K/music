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
  private String musicFileKey;

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
}
