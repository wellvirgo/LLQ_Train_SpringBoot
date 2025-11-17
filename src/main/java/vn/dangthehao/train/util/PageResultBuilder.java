package vn.dangthehao.train.util;

import org.springframework.data.domain.PageRequest;
import vn.dangthehao.train.dto.common.PageInfo;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.entity.PmhComponents1;

import java.util.List;

public class PageResultBuilder {
  public static PageResult<PmhComponents1> build(
      List<PmhComponents1> result, int page, int size, long totalElements, int totalPages) {
    PageInfo pageInfo =
        PageInfo.builder()
            .page(page)
            .size(size)
            .totalElements(totalElements)
            .totalPages(totalPages)
            .build();

    return PageResult.<PmhComponents1>builder().data(result).pageInfo(pageInfo).build();
  }
}
