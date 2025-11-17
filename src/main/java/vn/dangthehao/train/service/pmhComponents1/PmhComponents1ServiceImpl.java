package vn.dangthehao.train.service.pmhComponents1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.dto.component.NewDataComponent;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.DetailPmhComponentResponse;
import vn.dangthehao.train.dto.component.response.PmhComponentResponse;
import vn.dangthehao.train.dto.component.response.SearchPmhComponentResponse;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentActive;
import vn.dangthehao.train.enums.ComponentDisplay;
import vn.dangthehao.train.enums.ComponentStatus;
import vn.dangthehao.train.enums.SearchTech;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.mapper.PmhComponentMapper;
import vn.dangthehao.train.repository.PmhComponents1Repository;
import vn.dangthehao.train.service.pmhComponents1.dynamicSearch.*;
import vn.dangthehao.train.util.EnumUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Primary
public class PmhComponents1ServiceImpl implements PmhComponents1Service {
  PmhComponents1Repository pmhComponents1Repository;
  PmhComponentMapper pmhComponentMapper;
  ObjectMapper objectMapper;
  SearchComponentFactory searchComponentFactory;

  @Override
  public SearchPmhComponentResponse searchComponent(SearchPmhComponentRequest request) {
    SearchTech searchTech = request.getSearchTechOrDefault();
    SearchComponentService searchService =
        switch (searchTech) {
          case ENTITY_MANAGER_JPQL ->
              searchComponentFactory.getSearchService(EntityManagerJPQLSearch.class);
          case PROCEDURE -> searchComponentFactory.getSearchService(ProcedureSearch.class);
          default -> searchComponentFactory.getSearchService(JpaSpecificationSearch.class);
        };

    PageResult<PmhComponents1> pageResult = searchService.search(request);

    return buildSearchResponse(pageResult);
  }

  @Override
  @Transactional
  public PmhComponentResponse createComponent(CreatePmhComponentRequest request) {
    final int EFFECTIVE_DURATION = 5; // unit: days

    PmhComponents1 component = pmhComponentMapper.toComponentEntity(request);

    // Set default value for special fields
    component.setIsDisplay(ComponentDisplay.NOT_DISPLAY.getValue());
    component.setStatus(ComponentStatus.NEW.getValue());
    component.setIsActive(ComponentActive.NOT_ACTIVE.getValue());

    if (component.getEffectiveDate() != null) {
      component.setEndEffectiveDate(component.getEffectiveDate().plusDays(EFFECTIVE_DURATION));
    }

    PmhComponents1 savedComponent = pmhComponents1Repository.save(component);
    savedComponent.setNewData(createJsonComponent(savedComponent));

    return pmhComponentMapper.toComponentResponse(savedComponent);
  }

  @Override
  @Transactional
  public PmhComponentResponse updateComponent(Long id, UpdatePmhComponentRequest request) {
    PmhComponents1 existingComponent = getById(id);
    pmhComponentMapper.updateComponentFromRequest(request, existingComponent);
    existingComponent.setIsDisplay(
        EnumUtils.validateAndGetValue(ComponentDisplay.class, request.getIsDisplay()));
    existingComponent.setIsActive(
        EnumUtils.validateAndGetValue(ComponentActive.class, request.getIsActive()));
    existingComponent.setStatus(
        EnumUtils.validateAndGetValue(ComponentStatus.class, request.getStatus()));

    // Set new data after existingComponent was updated in persistence context
    existingComponent.setNewData(createJsonComponent(existingComponent));

    return pmhComponentMapper.toComponentResponse(existingComponent);
  }

  @Override
  public void deleteComponentById(Long id) {
    pmhComponents1Repository.deleteById(id);
  }

  public PmhComponents1 getById(Long id) {
    return pmhComponents1Repository
        .findById(id)
        .orElseThrow(
            () ->
                new AppException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    String.format("PmhComponents1 with id '%s' not found", id)));
  }

  private String createJsonComponent(PmhComponents1 component) {
    try {
      NewDataComponent newData = pmhComponentMapper.toNewDataComponent(component);
      return objectMapper.writeValueAsString(newData);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  private SearchPmhComponentResponse buildSearchResponse(PageResult<PmhComponents1> pageResult) {
    List<DetailPmhComponentResponse> componentsResponse =
        pageResult.getData().stream().map(pmhComponentMapper::toDetailComponentResponse).toList();

    return SearchPmhComponentResponse.builder()
        .components(componentsResponse)
        .page(pageResult.getPageInfo().getPage())
        .size(pageResult.getPageInfo().getSize())
        .totalElements(pageResult.getPageInfo().getTotalElements())
        .totalPages(pageResult.getPageInfo().getTotalPages())
        .build();
  }
}
