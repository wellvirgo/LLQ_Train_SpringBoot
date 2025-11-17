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
