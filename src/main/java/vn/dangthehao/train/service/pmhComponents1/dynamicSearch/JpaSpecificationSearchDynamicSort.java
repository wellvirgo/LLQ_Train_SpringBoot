package vn.dangthehao.train.service.pmhComponents1.dynamicSearch;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JpaSpecificationSearchDynamicSort implements SearchComponentService {
  PmhComponents1Repository pmhComponents1Repository;

  @Override
  public PageResult<PmhComponents1> search(SearchPmhComponentRequest request) {
    log.info(SearchTech.JPA_SPECIFICATION.name());
    Pageable pageable = getPageable(request);
    Page<PmhComponents1> pageResult =
        pmhComponents1Repository.findAll(PmhComponentSpecs.buildSpec(request), pageable);

    return PageResultBuilder.build(
        pageResult.getContent(),
        request.getPageOrDefault(),
        pageResult.getNumberOfElements(),
        pageResult.getTotalElements(),
        pageResult.getTotalPages());
  }

  private Pageable getPageable(SearchPmhComponentRequest request) {
    int page = request.getPageOrDefault();
    Long size = request.getSizeOrDefault();

    Sort sort =
        Sort.by(
            request.getSortDirectionOrDefault(),
            request.getSortFieldOrDefault().entityField(),
            "id");

    return PageRequest.of(page - 1, size.intValue(), sort);
  }
}
