package vn.dangthehao.train.dto.component.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullDetailComponentResponse extends PmhComponentResponse {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Long status;

  ComponentStatusResponse statusDetail;
  String newData;
}
