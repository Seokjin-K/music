package com.music.eneity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReleaseStatus {
  RELEASED("발매"),
  PENDING("대기"),
  DELETED("삭제");

  private final String description;
}
