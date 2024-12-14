package com.music.convert;

import com.music.constants.SortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SortTypeConvert implements Converter<String, SortType> {

  @Override
  public SortType convert(String type) {
    try {
      return SortType.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      log.error("잘못된 정렬 요청 : {} ", type);
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }
}
