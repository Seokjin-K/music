package com.music.repository;

import com.music.document.StreamingLog;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreamingLogRepository extends MongoRepository<StreamingLog, String> {

  Optional<StreamingLog> findBySessionId(String sessionId);
}