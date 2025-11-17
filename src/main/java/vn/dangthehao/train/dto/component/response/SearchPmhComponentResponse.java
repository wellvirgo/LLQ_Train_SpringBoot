package vn.dangthehao.train.dto.component.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.train.dto.common.PageInfo;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchPmhComponentResponse extends PageInfo {
  List<DetailPmhComponentResponse> components;
}
