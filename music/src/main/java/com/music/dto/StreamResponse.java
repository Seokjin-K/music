package com.music.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

@Getter
@Builder
public class StreamResponse {

  HttpHeaders headers;
  InputStreamResource resource;
}
