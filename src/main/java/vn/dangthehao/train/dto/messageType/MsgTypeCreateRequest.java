package vn.dangthehao.train.dto.messageType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MsgTypeCreateRequest {
  @NotBlank(message = "MsgType is must not be blank")
  String msgType;

  @Max(value = 100, message = "Max length of description is 100")
  String description;
}
