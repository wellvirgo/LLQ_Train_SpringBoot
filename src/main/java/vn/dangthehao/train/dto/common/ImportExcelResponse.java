package vn.dangthehao.train.dto.common;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.train.enums.ImportFileStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportExcelResponse {
  ImportFileStatus status;
  long total;
  long success;
  long failed;
  String errorReportName;
}
