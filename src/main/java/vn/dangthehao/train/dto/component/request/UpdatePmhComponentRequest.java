package vn.dangthehao.train.dto.component.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.dangthehao.train.annotation.ComponentDateRange;
import vn.dangthehao.train.enums.ComponentCheckToken;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ComponentDateRange
public class UpdatePmhComponentRequest implements HasDateRange {
  @NotBlank(message = "Component code must not be blank")
  @Size(max = 20)
  String componentCode;

  @NotBlank(message = "Component name must not be blank")
  @Size(max = 150)
  String componentName;

  @Size(max = 1500)
  String messageType;

  @Size(max = 1000)
  String connectionMethod;

  ComponentCheckToken checkToken;
  Long isDisplay;
  Long status;
  Long isActive;
  LocalDate effectiveDate;
  LocalDate endEffectiveDate;

  @Override
  public LocalDate getStartDate() {
    return effectiveDate;
  }

  @Override
  public LocalDate getEndDate() {
    return endEffectiveDate;
  }
}
