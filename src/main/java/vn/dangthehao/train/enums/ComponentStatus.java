package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ComponentStatus implements ValueEnum<Long> {
  NEW(1),
  PENDING(3),
  APPROVED(4),
  REJECTED(5),
  CANCELLED(7);

  long value;

  ComponentStatus(int value) {
    this.value = value;
  }

  @Override
  public Long getValue() {
    return value;
  }
}
