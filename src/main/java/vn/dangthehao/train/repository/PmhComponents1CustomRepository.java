package vn.dangthehao.train.repository;

import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;

import java.util.List;

public interface PmhComponents1CustomRepository {
  PageResult<PmhComponents1> findAllUseEntityManger(SearchPmhComponentRequest request);
  long count(SearchPmhComponentRequest request);
  PageResult<PmhComponents1> findAllUseProcedure(SearchPmhComponentRequest request);
}
