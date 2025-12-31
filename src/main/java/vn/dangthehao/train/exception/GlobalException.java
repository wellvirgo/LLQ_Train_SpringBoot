package vn.dangthehao.train.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.dangthehao.train.dto.common.ApiResponse;
import vn.dangthehao.train.dto.common.ErrorDetail;
import vn.dangthehao.train.util.ApiResponseBuilder;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Slf4j
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

    switch (cause) {
      case InvalidFormatException ife -> {
        return handleInvalidFormatEx(ife, errorCode);
      }
      case DateTimeParseException dtpe -> {
        return handleDateTimeParseEx(ex, dtpe, errorCode);
      }
      case DateTimeException dte -> {
        return handleDateTimeEx(ex, dte, errorCode);
      }
      default -> {}
    }

    // General res for other exception
    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), null));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handle(BadCredentialsException ex) {
    ErrorCode errorCode = ErrorCode.INVALID_CREDENTIALS;
    ApiResponse<Void> apiResponse =
        ApiResponseBuilder.error(errorCode.getCode(), errorCode.getMessage(), null);
    return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
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

  private ResponseEntity<ApiResponse<Void>> handleDateTimeParseEx(
      HttpMessageNotReadableException ex, DateTimeParseException dtpe, ErrorCode errorCode) {
    log.error("DateTimeParseException");
    ErrorDetail errorDetail = getErrorDetail(ex);
    errorDetail.setError(
        String.format(
            "Invalid date format '%s'. Expected format: 'yyyy-MM-dd'", dtpe.getParsedString()));

    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(
            ApiResponseBuilder.error(
                errorCode.getCode(), errorCode.getMessage(), List.of(errorDetail)));
  }

  private ResponseEntity<ApiResponse<Void>> handleDateTimeEx(
      HttpMessageNotReadableException ex, DateTimeException dte, ErrorCode errorCode) {
    log.error("DateTimeException");
    ErrorDetail errorDetail = getErrorDetail(ex);
    errorDetail.setError(dte.getMessage());

    return ResponseEntity.status(errorCode.getHttpStatus())
        .body(
            ApiResponseBuilder.error(
                errorCode.getCode(), errorCode.getMessage(), List.of(errorDetail)));
  }

  private ErrorDetail getErrorDetail(HttpMessageNotReadableException ex) {
    String object = "Request";
    String field = "Unknown";

    JsonMappingException jme = findJsonMappingException(ex);
    if (jme != null && !jme.getPath().isEmpty()) {
      JsonMappingException.Reference reference = jme.getPath().getFirst();
      object = reference.getFrom().getClass().getSimpleName();
      field = reference.getFieldName();
    }

    return ErrorDetail.builder().object(object).field(field).build();
  }

  private JsonMappingException findJsonMappingException(Throwable throwable) {
    Throwable cause = throwable.getCause();
    while (cause != null) {
      if (cause instanceof JsonMappingException jme) {
        return jme;
      }
      cause = cause.getCause();
    }

    return null;
  }
}
