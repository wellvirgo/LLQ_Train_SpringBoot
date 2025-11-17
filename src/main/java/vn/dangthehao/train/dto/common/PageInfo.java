package vn.dangthehao.train.dto.common;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageInfo {
    int page;
    int size;
    long totalElements;
    int totalPages;
}
