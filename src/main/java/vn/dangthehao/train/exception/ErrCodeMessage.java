package vn.dangthehao.train.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrCodeMessage {
  RESOURCE_NOT_FOUND("Resource {%s} not found"),
  FAILED_VALIDATION("Failed validation"),
  INVALID_ENUM_VALUE("Invalid enum value: '%s' for %s"),
  NOT_SUPPORTED("Not supported for: %s"),
  INVALID_CREDENTIALS("Username or password is incorrect"),
  INVALID_TOKEN("Invalid token"),
  FORBIDDEN("Access is denied"),
  ;

  String value;

  ErrCodeMessage(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
