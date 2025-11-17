package vn.dangthehao.train.dto.component;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewDataComponent {
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
