package vn.dangthehao.train.dto.component;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DraftImportRow {
  String componentCode;
  String componentName;
  String effectiveDate;
  String endEffectiveDate;
  String messageType;
  String connectionMethod;
  String status;
  StringBuilder errorDetails = new StringBuilder();

  public void appendError(String error) {
    errorDetails.append(error).append("\n");
  }
}
