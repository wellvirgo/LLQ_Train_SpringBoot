package vn.dangthehao.train.util;

import vn.dangthehao.train.dto.common.ErrorDetail;
import vn.dangthehao.train.enums.ResponseCode;
import vn.dangthehao.train.dto.common.ApiResponse;

import java.util.List;

public class ApiResponseBuilder {
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .code(ResponseCode.SUCCESS.getCode())
        .message(ResponseCode.SUCCESS.getMessage())
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> success() {
    return ApiResponse.<T>builder()
        .code(ResponseCode.SUCCESS.getCode())
        .message(ResponseCode.SUCCESS.getMessage())
        .build();
  }

  public static <T> ApiResponse<T> error(
      String code, String message, List<ErrorDetail> errorDetails) {
    return ApiResponse.<T>builder().code(code).message(message).errors(errorDetails).build();
  }
}
