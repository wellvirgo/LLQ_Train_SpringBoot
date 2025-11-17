package vn.dangthehao.train.util;

import vn.dangthehao.train.enums.ValueEnum;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;

public class EnumUtils {

  public static <E extends Enum<E> & ValueEnum<Long>> Long validateAndGetValue(
      Class<E> enumClass, Long value) {
    return fromValue(enumClass, value).getValue();
  }

  private static <E extends Enum<E> & ValueEnum<Long>> E fromValue(Class<E> enumClass, Long value) {
    for (E enumConstant : enumClass.getEnumConstants()) {
      if (enumConstant.getValue().equals(value)) {
        return enumConstant;
      }
    }

    throw new AppException(ErrorCode.INVALID_ENUM_VALUE, value, enumClass.getSimpleName());
  }
}
