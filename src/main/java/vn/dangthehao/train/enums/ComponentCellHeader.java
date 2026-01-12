package vn.dangthehao.train.enums;

public enum ComponentCellHeader {
  CODE("Component Code"),
  NAME("Component Name"),
  EFFECTIVE_DATE("Effective Date"),
  END_EFFECTIVE_DATE("End Effective Date"),
  MESSAGE_TYPE("Message Type"),
  CONNECTION_METHOD("Connection Method"),
  STATUS("Status"),
  ;

  private final String value;

  ComponentCellHeader(String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }
}
