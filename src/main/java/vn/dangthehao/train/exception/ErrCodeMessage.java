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
  MAXIMUM_FILE_SIZE_EXCEEDED("Maximum file size exceeded"),
  DATE_IN_PAST("Date in the past"),
  UNABLE_CREATE_DIRECTORY("Unable to create directory: %s"),
  UNABLE_CREATE_FILE("Unable to create file: %s"),
  UNABLE_READ_FILE("Unable to read file: %s"),
  INVALID_FILE_TEMPLATE("Invalid file template"),
  UNABLE_DELETE_FILE("Unable to delete file"),
  IO_ERROR("Unexpected file system error"),
  ;

  String value;

  ErrCodeMessage(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
