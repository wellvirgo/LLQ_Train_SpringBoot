package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ComponentStatus implements ValueEnum<Long> {
  NEW(1, "New"),
  PENDING(3, "Pending"),
  APPROVED(4, "Approved"),
  REJECTED(5, "Rejected"),
  CANCELLED(7, "Cancelled"),
  ;

  long value;
  @Getter String label;

  static final Map<Long, String> LABEL_MAP =
      Arrays.stream(ComponentStatus.values())
          .collect(Collectors.toMap(ComponentStatus::getValue, ComponentStatus::getLabel));

  ComponentStatus(int value, String label) {
    this.value = value;
    this.label = label;
  }

  @Override
  public Long getValue() {
    return value;
  }

  public static String getLabelByValue(long value) {
    return LABEL_MAP.get(value);
  }
}
