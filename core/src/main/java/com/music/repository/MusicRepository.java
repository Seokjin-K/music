package com.music.repository;

import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
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
  // TODO: 메서드 이름에 날짜 정보도 들어가도록 수정
}
