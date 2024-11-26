package com.music.service;

import com.music.constatns.StreamQuality;
import com.music.dto.StreamResponse;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.MusicRepository;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicStreamingService {

  private final MusicRepository musicRepository;
  private final FileStorageService fileStorageService;

  @Transactional(readOnly = true)
  public StreamResponse musicStreaming(Long musicId, HttpHeaders headers) {

    Music music = getMusic(musicId);

    StreamQuality streamQuality =
        StreamQuality.from(headers.getFirst("Network-Quality"));

    //streamingLogService.recordStart(musicId, userId); // TODO: 스트리밍 시작 기록

    InputStream audioStream =
        fileStorageService.getFileStream(music.getMusicFileKey());
    InputStreamResource resource = new InputStreamResource(audioStream);
    HttpHeaders responseHeaders = new HttpHeaders(); // 응답 헤더 객체 생성

    // 브라우저에서 오디오 재생을 자동으로 지원
    responseHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));
    
    return StreamResponse.builder()
        .headers(responseHeaders)
        .resource(resource)
        .build();
  }

  private Music getMusic(Long musicId) {
    return musicRepository
        .findByIdAndReleaseStatus(musicId, ReleaseStatus.RELEASED)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경
  }
}
