package com.music.convert;

import com.music.constants.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SortDirectionConvert implements Converter<String, SortDirection> {

  @Override
  public SortDirection convert(String direction) {
    try {
      return SortDirection.valueOf(direction.toUpperCase());
    } catch (IllegalArgumentException e) {
      log.error("잘못된 정렬 방향 요청 : {} ", direction);
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }
}
