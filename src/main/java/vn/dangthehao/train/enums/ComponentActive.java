package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ComponentActive implements ValueEnum<Long> {
  ACTIVE(1),
  NOT_ACTIVE(0);

  long value;

  ComponentActive(int value) {
    this.value = value;
  }

  @Override
  public Long getValue() {
    return value;
  }
}
