package vn.dangthehao.train.service.pmhComponents1.dynamicSearch;

import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;

public interface SearchComponentService {
    PageResult<PmhComponents1> search(SearchPmhComponentRequest request);
}
