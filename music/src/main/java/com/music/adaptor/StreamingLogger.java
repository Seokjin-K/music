package com.music.adaptor;

import com.music.streaming.dto.StreamingEndRequest;
import com.music.streaming.dto.StreamingStartRequest;
import com.music.streaming.dto.StreamingStartResponse;

public interface StreamingLogger {

  StreamingStartResponse logStart(StreamingStartRequest request);
  void logEnd(StreamingEndRequest request);
}
