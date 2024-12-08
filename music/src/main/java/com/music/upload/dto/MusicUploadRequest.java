package com.music.upload.dto;

import com.music.eneity.constants.Genre;
import java.time.LocalDate;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MusicUploadRequest {

  @NotNull(message = "앨범 아이디는 필수입니다.")
  private Long albumId;

  @NotBlank(message = "제목은 필수입니다.")
  private String title;

  @NotNull(message = "트랙 번호는 필수입니다.")
  @Min(value = 1, message = "트랙 번호는 1 이상이어야 합니다.")
  private Integer trackNumber;

  @NotNull(message = "재생 시간은 필수입니다.")
  @Min(value = 1, message = "재생 시간은 1초 이상이어야 합니다.")
  private Integer duration;

  @NotNull(message = "발매일은 필수입니다.")
  @Future(message = "발매일은 미래여야 합니다.")
  private LocalDate releaseAt;

  @NotNull(message = "장르는 필수입니다.")
  private Genre genre;

  @NotNull(message = "타이틀 곡 여부는 필수입니다.")
  private Boolean titleTrack;
}
