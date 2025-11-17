package vn.dangthehao.train.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.dto.common.ErrorDetail;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalException {
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException e) {
    ApiResponse<Void> errorResponse =
        ApiResponseBuilder.error(e.getErrorCode().getCode(), e.getMessage(), null);

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handle(MethodArgumentNotValidException ex) {
    ErrorCode errorCode = ErrorCode.FAILED_VALIDATION;

    List<ErrorDetail> errorDetails = extractMethodArgumentErrors(ex);
    ApiResponse<Void> errorResponse =
        ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), errorDetails);

    return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handle(HttpMessageNotReadableException ex) {
    ErrorCode errorCode = ErrorCode.FAILED_VALIDATION;
    Throwable cause = ex.getMostSpecificCause();

    if (cause instanceof InvalidFormatException ife) {
      return handleInvalidFormatEx(ife, errorCode);
    }

    // General res for other exception
    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), null));
  }

  private List<ErrorDetail> extractMethodArgumentErrors(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getAllErrors().stream()
        .map(
            error -> {
              if (error instanceof FieldError fieldError) {
                return ErrorDetail.builder()
                    .object(fieldError.getObjectName())
                    .field(fieldError.getField())
                    .error(fieldError.getDefaultMessage())
                    .build();
              }

              return ErrorDetail.builder()
                  .object(error.getObjectName())
                  .error(error.getDefaultMessage())
                  .build();
            })
        .toList();
  }

  private ResponseEntity<ApiResponse<Void>> handleInvalidFormatEx(
      InvalidFormatException ife, ErrorCode errorCode) {
    JsonMappingException.Reference reference = ife.getPath().getFirst();
    String objectName = reference.getFrom().getClass().getSimpleName();
    String fieldName = reference.getFieldName();

    ErrorDetail errorDetail = ErrorDetail.builder().object(objectName).field(fieldName).build();
    errorDetail.setError(buildErrorMessage(ife));

    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(
            ApiResponseBuilder.error(
                errorCode.getCode(), errorCode.getMessage(), List.of(errorDetail)));
  }

  private String buildErrorMessage(InvalidFormatException ife) {
    Class<?> targetType = ife.getTargetType();
    Object value = ife.getValue();

    if (targetType.isEnum()) {
      return String.format(
          "Invalid value '%s'. Accepted value: %s",
          value, Arrays.toString(ife.getTargetType().getEnumConstants()));
    } else if (targetType.equals(LocalDate.class)) {
      return String.format("Invalid date format '%s'. Expected format: 'yyyy-MM-dd'", value);
    }

    return String.format(
        "Invalid value '%s'. Expected type: %s", value, targetType.getSimpleName());
  }
}
