package com.music.repository;

import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  Optional<Music> findByIdAndReleaseStatus(Long musicId, ReleaseStatus releaseStatus);
}
