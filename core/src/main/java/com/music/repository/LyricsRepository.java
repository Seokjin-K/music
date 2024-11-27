package com.music.repository;

import com.music.eneity.Lyrics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LyricsRepository extends JpaRepository<Lyrics, Long> {

  Optional<Lyrics> findByMusicId(Long musicId);
}
