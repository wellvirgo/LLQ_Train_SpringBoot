package vn.dangthehao.train.service.pmhComponents1;

import jakarta.servlet.http.HttpServletResponse;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.ComponentStatusResponse;
import vn.dangthehao.train.dto.component.response.FullDetailComponentResponse;
import vn.dangthehao.train.dto.component.response.PmhComponentResponse;
import vn.dangthehao.train.dto.component.response.SearchPmhComponentResponse;
import vn.dangthehao.train.entity.PmhComponents1;

import java.util.List;

public interface PmhComponents1Service {
  SearchPmhComponentResponse searchComponent(SearchPmhComponentRequest request);

  PmhComponentResponse createComponent(CreatePmhComponentRequest request);

  PmhComponentResponse updateComponent(Long id, UpdatePmhComponentRequest request);

  void deleteComponentById(Long id);

  FullDetailComponentResponse getComponentById(Long id);

  void batchCreateComponents(List<PmhComponents1> pmhComponents1);

  int updateComponentStatus(List<Long> ids, String statusLabel);

  List<ComponentStatusResponse> getAllStatuses();
}
