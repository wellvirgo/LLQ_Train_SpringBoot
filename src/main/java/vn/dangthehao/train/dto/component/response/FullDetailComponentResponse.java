package vn.dangthehao.train.dto.component.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.train.dto.messageType.MsgTypeResponse;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullDetailComponentResponse extends PmhComponentResponse {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  Long status;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  String messageType;

  ComponentStatusResponse statusDetail;
  MsgTypeResponse msgTypeDetail;
  String newData;
}
