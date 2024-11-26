package com.music.service;

import com.music.dto.StreamResponse;
import com.music.eneity.Music;
import com.music.eneity.constants.ReleaseStatus;
import com.music.repository.MusicRepository;
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
  private FileStorageService fileStorageService;

  @Test
  @DisplayName("음원 스트리밍 - 성공")
  void musicStreaming_Success() {
    // given
    Long musicId = 1L;
    String fileKey = "test.mp3";

    Music music = Music.builder()
        .id(musicId)
        .musicFileKey(fileKey)
        .build();

    HttpHeaders headers = new HttpHeaders();

    // musicRepository 동작 정의
    when(musicRepository.findByIdAndReleaseStatus(musicId, ReleaseStatus.RELEASED))
        .thenReturn(Optional.of(music)); // 음원 조회 시 생성한 music 객체 반환하도록 설정

    // fileStorageService 동작 정의
    when(fileStorageService.getFileStream(fileKey))
        .thenReturn(new ByteArrayInputStream(new byte[0])); // 빈 스트림 반환하도록 설정
    // ByteArrayInputStream : 실제 리소스가 없이도 동작 가능

    // when
    StreamResponse response = musicStreamingService.musicStreaming(musicId, headers);

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
        musicStreamingService.musicStreaming(1L, new HttpHeaders()))
        .isInstanceOf(RuntimeException.class); // TODO: CustomException 으로 변경
  }
}