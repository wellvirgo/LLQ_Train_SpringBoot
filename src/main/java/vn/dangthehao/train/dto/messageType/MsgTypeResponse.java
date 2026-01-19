package vn.dangthehao.train.dto.messageType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MsgTypeResponse {
    String msgType;
    String description;
}
