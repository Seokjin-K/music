package com.music.streaming.service;

import com.music.streaming.dto.LyricsResponse;
import com.music.streaming.dto.StreamResponse;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.LyricsRepository;
import com.music.repository.MusicRepository;
import com.music.adaptor.FileStorage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicStreamingService {

  private final MusicRepository musicRepository;
  private final LyricsRepository lyricsRepository;
  private final FileStorage fileStorageService;

  @Transactional(readOnly = true)
  public StreamResponse musicStreaming(Long musicId, HttpHeaders headers) {

    /*Music music = getMusic(musicId);

    InputStream audioStream =
        fileStorageService.getFileStream(music.getMusicFileKey());
    InputStreamResource resource = new InputStreamResource(audioStream);

    HttpHeaders responseHeaders = new HttpHeaders(); // 응답 헤더 객체 생성
    responseHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));

    return StreamResponse.builder()
        .headers(responseHeaders)
        .resource(resource)
        .build();*/
    return null;
  }

  public LyricsResponse getLyrics(Long musicId) {
    return lyricsRepository.findByMusicId(musicId)
        .map(lyrics -> LyricsResponse.builder()
            .format(lyrics.getLyricsFormat())
            .content(getLyricsContent(lyrics.getLyricsFileKey()))
            .build())
        .orElse(LyricsResponse.builder()
            .format(null)
            .content("가사 정보가 없습니다.")
            .build());
  }

  private Music getMusic(Long musicId) {
    return musicRepository
        .findByIdAndReleaseStatus(musicId, ReleaseStatus.RELEASED)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경
  }

  private String getLyricsContent(String fileKey) {
    try (InputStream inputStream = fileStorageService.getFileStream(fileKey);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(); // TODO: CustomException 으로 변경
    }
  }
}
