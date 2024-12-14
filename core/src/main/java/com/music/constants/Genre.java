package com.music.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {
  BALLAD("발라드"),
  DANCE("댄스"),
  HIPHOP("힙합"),
  RNB("R&B"),
  INDIE("인디"),
  ROCK("락");

  private final String description;
}
