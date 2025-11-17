package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ResponseCode {
  SUCCESS("200", "Success"),
  ERROR("400", "Error");

  String code;
  String message;

  ResponseCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
