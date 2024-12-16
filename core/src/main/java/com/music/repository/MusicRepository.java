package com.music.repository;

import com.music.eneity.Music;
import com.music.constants.ReleaseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  Optional<Music> findByIdAndReleaseStatus(Long musicId, ReleaseStatus releaseStatus);

  @Query("SELECT "
      + "m.title as title, "
      + "ar.name as artistName, "
      + "m.duration as duration, "
      + "m.releaseAt as releaseAt, "
      + "m.genre as genre, "
      + "m.titleTrack as titleTrack "
      + "FROM Music m "
      + "JOIN m.album a "
      + "JOIN a.artist ar "
      + "WHERE m.id = :musicId and m.releaseStatus = :releaseStatus")
  Optional<MusicResponseProjection> findByIdAndReleaseStatusWithAlbumAndArtist(
      @Param("musicId") Long musicId,
      @Param("releaseStatus") ReleaseStatus releaseStatus
  );

  @Query("SELECT m FROM Music m "
      + "JOIN FETCH m.album a "
      + "JOIN FETCH a.artist "
      + "WHERE m.id IN :musicIds")
  List<Music> findAllByIdWithAlbumAndArtist(List<Long> musicIds);

  @Modifying
  @Query("UPDATE Music m "
      + "SET m.releaseStatus = :newStatus "
      + "WHERE m.releaseStatus = :currentStatus "
      + "AND m.releaseAt <= NOW()")
  int updatePendingToReleasedByBeforeNow(
      @Param("currentStatus") ReleaseStatus currentStatus,
      @Param("newStatus") ReleaseStatus newStatus
  );

  @Modifying
  @Query("UPDATE Music m "
      + "SET m.releaseStatus = :status "
      + "WHERE m.id = :musicId AND m.releaseStatus != :status")
  int updateMusicStatus(
      @Param("musicId") Long musicId,
      @Param("status") ReleaseStatus status
  );
}
