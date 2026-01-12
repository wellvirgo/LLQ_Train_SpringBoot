package vn.dangthehao.train.service.pmhComponents1.dynamicSearch;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.SearchTech;
import vn.dangthehao.train.repository.PmhComponents1Repository;
import vn.dangthehao.train.specification.PmhComponentSpecs;
import vn.dangthehao.train.util.PageResultBuilder;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaSpecificationSearch implements SearchComponentService {
  PmhComponents1Repository pmhComponents1Repository;

  @Override
  public PageResult<PmhComponents1> search(SearchPmhComponentRequest request) {
    int page = request.getPageOrDefault();
    Long size = request.getSizeOrDefault();

    Pageable pageable = PageRequest.of(page - 1, size.intValue(), Sort.by(Sort.Direction.ASC, "id"));

    Page<PmhComponents1> pageResult =
        pmhComponents1Repository.findAll(PmhComponentSpecs.buildSpec(request), pageable);

    log.info(SearchTech.JPA_SPECIFICATION.name());

    return PageResultBuilder.build(
        pageResult.getContent(),
        page,
        pageResult.getNumberOfElements(),
        pageResult.getTotalElements(),
        pageResult.getTotalPages());
  }
}
