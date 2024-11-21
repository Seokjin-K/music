package com.music.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class ApiResponse <T>{
  private Status status;
  private String message;
  private T data;

  @Getter
  @RequiredArgsConstructor
  public enum Status{
    SUCCESS("success"),
    ERROR("error");

    private final String value;
  }

  public static <T> ApiResponse<T> success(T data){
    return ApiResponse.<T>builder()
        .status(Status.SUCCESS)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> success(T data, String message){
    return ApiResponse.<T>builder()
        .status(Status.SUCCESS)
        .message(message)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> error(String message){
    return ApiResponse.<T>builder()
        .status(Status.ERROR)
        .message(message)
        .build();
  }
}
