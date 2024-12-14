package com.music.repository;

import com.music.eneity.Music;
import com.music.constants.ReleaseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  Optional<Music> findByIdAndReleaseStatus(Long musicId, ReleaseStatus releaseStatus);

  @Query("SELECT m FROM Music m "
      + "JOIN FETCH m.album a "
      + "JOIN FETCH a.artist "
      + "WHERE m.id IN :musicIds")
  List<Music> findAllByIdWithAlbumAndArtist(List<Long> musicIds);
}
