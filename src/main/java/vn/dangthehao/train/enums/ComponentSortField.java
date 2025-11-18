package vn.dangthehao.train.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ComponentSortField {
  ID("id", "ID"),
  COMPONENT_CODE("componentCode", "COMPONENT_CODE"),
  COMPONENT_NAME("componentName", "COMPONENT_NAME"),
  EFFECTIVE_DATE("effectiveDate", "EFFECTIVE_DATE"),
  END_EFFECTIVE_DATE("endEffectiveDate", "END_EFFECTIVE_DATE"),
  ;

  String entityField;
  String dbColumn;

  ComponentSortField(String field, String dbColumn) {
    this.entityField = field;
    this.dbColumn = dbColumn;
  }

  public String entityField() {
    return entityField;
  }

  public String dbColumn() {
    return dbColumn;
  }
}
