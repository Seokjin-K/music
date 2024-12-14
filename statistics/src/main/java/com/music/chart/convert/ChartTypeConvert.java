package com.music.chart.convert;

import com.music.constants.ChartType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

// Spring MVC 는 요청이 들어오면 등록된 Converter 들을 확인
// 해당 타입 변환이 필요할 때 (예: String -> ChartType) 자동으로 convert 메서드를 호출
// 이 과정은 @PathVariable, @RequestParam 등의 바인딩 시점에 자동으로 이루어짐
// 따라서 별도로 convert 메서드를 직접 호출할 필요 없이,
// 컨트롤러에서 ChartType 을 파라미터로 받으면 자동으로 변환됨
@Slf4j
@Component
public class ChartTypeConvert implements Converter<String, ChartType> {

  @Override
  public ChartType convert(String source) {
    try {
      return ChartType.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException e) {
      log.warn("잘못된 ChartType 요청 : {}", source);
      throw new RuntimeException(e); // TODO: CustomException 으로 변경
    }
  }
}
