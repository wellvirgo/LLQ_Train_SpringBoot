package vn.dangthehao.train.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
  FAILED_VALIDATION("50", ErrCodeMessage.FAILED_VALIDATION.value(), HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_FOUND("51", ErrCodeMessage.RESOURCE_NOT_FOUND.value(), HttpStatus.NOT_FOUND),
  INVALID_ENUM_VALUE("52", ErrCodeMessage.INVALID_ENUM_VALUE.value(), HttpStatus.BAD_REQUEST),
  NOT_SUPPORTED("53", ErrCodeMessage.NOT_SUPPORTED.value(), HttpStatus.BAD_REQUEST),
  INVALID_CREDENTIALS("54", ErrCodeMessage.INVALID_CREDENTIALS.value(), HttpStatus.BAD_REQUEST),
  UNAUTHENTICATED("55", ErrCodeMessage.INVALID_TOKEN.value(), HttpStatus.UNAUTHORIZED),
  FORBIDDEN("56", ErrCodeMessage.FORBIDDEN.value(), HttpStatus.FORBIDDEN),
  MAXIMUM_UPLOAD_SIZE(
      "57", ErrCodeMessage.MAXIMUM_FILE_SIZE_EXCEEDED.value(), HttpStatus.BAD_REQUEST),
  DATE_IN_PAST("58", ErrCodeMessage.DATE_IN_PAST.value(), HttpStatus.BAD_REQUEST),
  UNABLE_CREATE_DIRECTORY(
      "59", ErrCodeMessage.UNABLE_CREATE_DIRECTORY.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  UNABLE_CREATE_FILE(
      "60", ErrCodeMessage.UNABLE_CREATE_FILE.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  UNABLE_READ_FILE("61", ErrCodeMessage.UNABLE_READ_FILE.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  INVALID_FILE_TEMPLATE("62", ErrCodeMessage.INVALID_FILE_TEMPLATE.value(), HttpStatus.BAD_REQUEST),
  UNABLE_DELETE_FILE(
      "63", ErrCodeMessage.UNABLE_DELETE_FILE.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  IO_ERROR("64", ErrCodeMessage.IO_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  DATA_BATCH_UPDATE_FAILED("65", ErrCodeMessage.DATA_BATCH_UPDATE_FAILED.value(), HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  final String code;
  final String message;
  final HttpStatus httpStatus;

  ErrorCode(String code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}
