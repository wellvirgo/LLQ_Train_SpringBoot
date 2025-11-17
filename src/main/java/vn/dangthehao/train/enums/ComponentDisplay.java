package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ComponentDisplay implements ValueEnum<Long> {
  DISPLAY(1),
  NOT_DISPLAY(0);

  long value;

  ComponentDisplay(int value) {
    this.value = value;
  }

  @Override
  public Long getValue() {
    return value;
  }
}
