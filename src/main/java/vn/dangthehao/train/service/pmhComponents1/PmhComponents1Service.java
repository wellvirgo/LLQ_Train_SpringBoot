package vn.dangthehao.train.service.pmhComponents1;

import jakarta.servlet.http.HttpServletResponse;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.PmhComponentResponse;
import vn.dangthehao.train.dto.component.response.SearchPmhComponentResponse;
import vn.dangthehao.train.entity.PmhComponents1;

public interface PmhComponents1Service {
  SearchPmhComponentResponse searchComponent(SearchPmhComponentRequest request);

  PmhComponentResponse createComponent(CreatePmhComponentRequest request);

  PmhComponentResponse updateComponent(Long id, UpdatePmhComponentRequest request);

  void deleteComponentById(Long id);

  PmhComponents1 getComponentById(Long id);

  void exportToExcel(HttpServletResponse response, SearchPmhComponentRequest request);
}
