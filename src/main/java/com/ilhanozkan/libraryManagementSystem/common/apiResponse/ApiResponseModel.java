package com.ilhanozkan.libraryManagementSystem.common.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponseModel<T> {
  private boolean success;
  private String message;
  private T data;

  public static <T> ApiResponseModel<T> success(T data) {
    return new ApiResponseModel<>(true, "Success", data);
  }

  public static <T> ApiResponseModel<T> success(String message, T data) {
    return new ApiResponseModel<>(true, message, data);
  }

  public static <T> ApiResponseModel<T> error(String message) {
    return new ApiResponseModel<>(false, message, null);
  }
}
