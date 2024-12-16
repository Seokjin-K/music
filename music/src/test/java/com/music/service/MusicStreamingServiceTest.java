package com.music.service;

import com.music.adaptor.FileStorage;
import com.music.dto.streaming.LyricsResponse;
import com.music.dto.streaming.StreamResponse;
import com.music.eneity.Lyrics;
import com.music.eneity.Music;
import com.music.eneity.constants.LyricsFormat;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.LyricsRepository;
import com.music.repository.MusicRepository;
import com.music.service.streaming.MusicStreamingService;
import java.io.InputStream;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MusicStreamingServiceTest {

  @InjectMocks
  private MusicStreamingService musicStreamingService;

  @Mock
  private MusicRepository musicRepository;

  @Mock
  private FileStorage fileStorageService;

  @Mock
  private LyricsRepository lyricsRepository;

  @Test
  @DisplayName("음원 스트리밍 - 성공")
  void musicStreaming_Success() {
    // given
    Long musicId = 1L;
    String fileKey = "test.mp3";

    Music music = Music.builder()
        .id(musicId)
        //.musicFileKey(fileKey)
        .build();

    HttpHeaders headers = new HttpHeaders();

    when(musicRepository.findByIdAndReleaseStatus(musicId, ReleaseStatus.RELEASED))
        .thenReturn(Optional.of(music));

    when(fileStorageService.getFileStream(fileKey))
        .thenReturn(new ByteArrayInputStream(new byte[0]));

    // when
    StreamResponse response = musicStreamingService.musicStreaming(musicId, "HIGH");

    // then
    assertThat(response.getHeaders().getContentType())
        .isEqualTo(MediaType.parseMediaType("audio/mpeg"));
    assertThat(response.getResource())
        .isInstanceOf(InputStreamResource.class);
  }

  @Test
  @DisplayName("음원 스트리밍 - 실패")
  void musicStreaming_Fail() {
    // given
    when(musicRepository.findByIdAndReleaseStatus(anyLong(), any(ReleaseStatus.class)))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() ->
        musicStreamingService.musicStreaming(1L, "HIGH"))
        .isInstanceOf(RuntimeException.class); // TODO: CustomException 으로 변경
  }

  @Test
  @DisplayName("가사 가져오기 - 성공")
  void getLyrics_Success() {
    // given
    Long musicId = 1L;
    String fileKey = "test-key";
    String content = "[00:01.00]첫번째 가사\n[00:05.00]두번째 가사";

    Lyrics lyrics = Lyrics.builder()
        .lyricsFileKey(fileKey)
        .lyricsFormat(LyricsFormat.LRC)
        .build();

    InputStream inputStream = new ByteArrayInputStream(content.getBytes());

    when(lyricsRepository.findByMusicId(musicId))
        .thenReturn(Optional.of(lyrics));
    when(fileStorageService.getFileStream(fileKey))
        .thenReturn(inputStream);

    // when
    LyricsResponse response = musicStreamingService.getLyrics(musicId);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getFormat()).isEqualTo(LyricsFormat.LRC);
    assertThat(response.getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("가사 가져오기 - 실패")
  void getLyrics_Fail() {
    // given
    Long musicId = 1L;
    when(lyricsRepository.findByMusicId(musicId))
        .thenReturn(Optional.empty());

    // when
    LyricsResponse response = musicStreamingService.getLyrics(musicId);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getFormat()).isNull();
    assertThat(response.getContent()).isEqualTo("가사 정보가 없습니다.");
  }
}