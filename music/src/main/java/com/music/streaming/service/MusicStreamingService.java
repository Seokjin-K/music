package com.music.streaming.service;

import com.music.constatns.AudioQuality;
import com.music.streaming.dto.LyricsResponse;
import com.music.streaming.dto.StreamResponse;
import com.music.eneity.Music;
import com.music.constants.ReleaseStatus;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
  public StreamResponse musicStreaming(Long musicId, String quality) {

    Music music = getMusic(musicId); // 음원 체크
    String musicFileKey = getFileKeyByQuality(quality, music); // 품질에 맞는 음원 파일키 가져오기

    InputStream audioStream = fileStorageService.getFileStream(musicFileKey);
    HttpHeaders responseHeaders = createResponseHeaders(musicId, music.getDuration());

    return StreamResponse.builder()
        .headers(responseHeaders)
        .resource(new InputStreamResource(audioStream))
        .build();
  }

  @Transactional(readOnly = true)
  public LyricsResponse getLyrics(Long musicId) {
    return lyricsRepository.findByMusicId(musicId)
        .map(lyrics -> LyricsResponse.builder()
            .format(lyrics.getLyricsFormat())
            .content(getLyricsContent(lyrics.getLyricsFileKey()))
            .build())
        .orElseGet(() -> LyricsResponse.builder().build());
  }

  private Music getMusic(Long musicId) {
    return musicRepository
        .findByIdAndReleaseStatus(musicId, ReleaseStatus.RELEASED)
        .orElseThrow(RuntimeException::new); // TODO: CustomException 으로 변경
  }

  private static String getFileKeyByQuality(String quality, Music music) {
    AudioQuality streamQuality = AudioQuality.from(quality);
    return streamQuality.getFileKey(music);
  }

  private HttpHeaders createResponseHeaders(Long musicId, Integer musicDuration) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.parseMediaType("audio/mpeg"));
    responseHeaders.add("Music-Id", String.valueOf(musicId));
    responseHeaders.add("Music-Duration", String.valueOf(musicDuration));
    return responseHeaders;
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
