package vn.dangthehao.train.service.pmhComponents1.dynamicSearch;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SearchComponentFactory {
  Map<String, SearchComponentService> searchServiceMap = new HashMap<>();

  public SearchComponentFactory(List<SearchComponentService> searchServices) {
    for (SearchComponentService service : searchServices) {
      searchServiceMap.put(service.getClass().getSimpleName(), service);
    }
  }

  public SearchComponentService getSearchService(
      Class<? extends SearchComponentService> searchServiceClass) {
    String className = searchServiceClass.getSimpleName();
    SearchComponentService searchService = searchServiceMap.get(className);
    if (searchService == null) {
      String message = "search tech " + className;
      throw new AppException(ErrorCode.NOT_SUPPORTED, message);
    }

    return searchService;
  }
}
