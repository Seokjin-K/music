package com.music.dto.streaming;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

@Getter
@Builder
public class StreamResponse {

  private HttpHeaders headers;
  private InputStreamResource resource;
}
