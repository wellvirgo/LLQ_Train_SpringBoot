package vn.dangthehao.train.service.pmhComponents1.dynamicSearch;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.SearchTech;
import vn.dangthehao.train.repository.PmhComponents1Repository;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityManagerJPQLSearch implements SearchComponentService {
  PmhComponents1Repository pmhComponents1Repository;

  @Override
  public PageResult<PmhComponents1> search(SearchPmhComponentRequest request) {
    log.info(SearchTech.ENTITY_MANAGER_JPQL.name());
    return pmhComponents1Repository.findAllUseEntityManger(request);
  }
}
