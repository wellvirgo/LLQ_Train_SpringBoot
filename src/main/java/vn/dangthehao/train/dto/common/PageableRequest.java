package vn.dangthehao.train.dto.common;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableRequest {
    @Min(value = 1, message = "Page number must be greater than 0")
    Integer page;
    @Min(value = 0, message = "Page size must be at least 0")
    Integer size;
}
