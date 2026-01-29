package vn.dangthehao.train.dto.component.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchingUpdateStatusRequest {
    List<Long> ids;
    String status;
}
