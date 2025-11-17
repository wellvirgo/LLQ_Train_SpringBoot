package vn.dangthehao.train.dto.component.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailPmhComponentResponse {
  Long id;
  String componentCode;
  String componentName;
  String messageType;
  String connectionMethod;
  String checkToken;
  Long status;
  @JsonRawValue String newData;
  LocalDate effectiveDate;
  LocalDate endEffectiveDate;
}
