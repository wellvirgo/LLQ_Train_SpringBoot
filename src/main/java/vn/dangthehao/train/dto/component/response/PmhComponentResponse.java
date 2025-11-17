package vn.dangthehao.train.dto.component.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PmhComponentResponse {
  Long id;
  String componentCode;
  String componentName;
  String messageType;
  String connectionMethod;
  String checkToken;
  Long isDisplay;
  Long status;
  Long isActive;
  LocalDate effectiveDate;
  LocalDate endEffectiveDate;
}
