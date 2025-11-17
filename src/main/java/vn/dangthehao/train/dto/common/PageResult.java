package vn.dangthehao.train.dto.common;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResult<T> {
  List<T> data;
  PageInfo pageInfo;
}
