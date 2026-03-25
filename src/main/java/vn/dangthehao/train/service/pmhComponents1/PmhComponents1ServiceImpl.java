package vn.dangthehao.train.service.pmhComponents1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.dangthehao.train.dto.common.PageInfo;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.CreatePmhComponentRequest;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.dto.component.NewDataComponent;
import vn.dangthehao.train.dto.component.request.UpdatePmhComponentRequest;
import vn.dangthehao.train.dto.component.response.*;
import vn.dangthehao.train.dto.messageType.MsgTypeResponse;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.entity.UserActionLog;
import vn.dangthehao.train.enums.ComponentActive;
import vn.dangthehao.train.enums.ComponentDisplay;
import vn.dangthehao.train.enums.ComponentStatus;
import vn.dangthehao.train.enums.SearchTech;
import vn.dangthehao.train.exception.AppException;
import vn.dangthehao.train.exception.ErrorCode;
import vn.dangthehao.train.mapper.PmhComponentMapper;
import vn.dangthehao.train.repository.PmhComponents1Repository;
import vn.dangthehao.train.service.export.ExportExcelService;
import vn.dangthehao.train.service.kafka.UserActionLogProcedure;
import vn.dangthehao.train.service.messageType.MessageTypeService;
import vn.dangthehao.train.service.pmhComponents1.dynamicSearch.*;
import vn.dangthehao.train.util.EnumUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
  ExportExcelService exportExcelService;
  MessageTypeService messageTypeService;
  EntityManager entityManager;
  UserActionLogProcedure userActionLogProcedure;

  @Override
  public SearchPmhComponentResponse searchComponent(SearchPmhComponentRequest request) {
    SearchTech searchTech = request.getSearchTechOrDefault();
    SearchComponentService searchService =
        switch (searchTech) {
          case ENTITY_MANAGER -> searchComponentFactory.getSearchService(EntityManagerSearch.class);
          case PROCEDURE -> searchComponentFactory.getSearchService(ProcedureSearch.class);
          default ->
              searchComponentFactory.getSearchService(JpaSpecificationSearchDynamicSort.class);
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
    LocalDateTime createTime = LocalDateTime.now();

    UserActionLog userActionLog =
        UserActionLog.builder()
            .action("CREATE")
            .log("add a new pmh component id: " + savedComponent.getId())
            .timestamp(createTime)
            .build();
    userActionLogProcedure.sendUserActionLog(userActionLog);

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

  @Override
  public FullDetailComponentResponse getComponentById(Long id) {
    PmhComponents1 component = getById(id);
    FullDetailComponentResponse detailComponent =
        pmhComponentMapper.toFullDetailComponentResponse(component);

    ComponentStatusResponse statusDetail =
        ComponentStatusResponse.builder()
            .label(ComponentStatus.getLabelByValue(component.getStatus()))
            .value(component.getStatus())
            .build();
    detailComponent.setStatusDetail(statusDetail);

    MsgTypeResponse msgTypeDetail =
        messageTypeService.getMessageTypeByMsgType(component.getMessageType());
    detailComponent.setMsgTypeDetail(msgTypeDetail);

    return detailComponent;
  }

  @Override
  public void exportToExcel(HttpServletResponse response, SearchPmhComponentRequest request) {
    SearchComponentService searchService =
        searchComponentFactory.getSearchService(ProcedureSearch.class);
    long totalElements = searchService.search(request).getPageInfo().getTotalElements();
    request.setSize(totalElements);

    List<PmhComponents1> components = searchService.search(request).getData();
    exportExcelService.exportComponents(response, components);
  }

  @Override
  public void batchCreateComponents(List<PmhComponents1> pmhComponents1) {
    pmhComponents1Repository.saveAllAndFlush(pmhComponents1);
    entityManager.clear();
  }

  @Override
  @Transactional
  public int updateComponentStatus(List<Long> ids, String statusLabel) {
    int result = 0;
    if (ids == null || ids.isEmpty()) return result;

    Long status = ComponentStatus.getValueByLabel(statusLabel);
    if (Objects.isNull(status))
      throw new AppException(
          ErrorCode.INVALID_ENUM_VALUE, statusLabel, ComponentStatus.class.getSimpleName());

    try {
      result = pmhComponents1Repository.updateStatusForIds(ids, status);
    } catch (Exception e) {
      throw new AppException(ErrorCode.DATA_BATCH_UPDATE_FAILED);
    }

    return result;
  }

  @Override
  public List<ComponentStatusResponse> getAllStatuses() {
    return Arrays.stream(ComponentStatus.values())
        .map(
            ct ->
                ComponentStatusResponse.builder().value(ct.getValue()).label(ct.getLabel()).build())
        .toList();
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
    List<PmhComponents1> components = pageResult.getData();

    Set<String> uniqueMsgTypes =
        components.stream()
            .map(PmhComponents1::getMessageType)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    Map<String, MsgTypeResponse> msgTypeMap =
        messageTypeService.getMessageTypeMapByMsgTypes(uniqueMsgTypes);

    List<DetailPmhComponentResponse> componentsResponse =
        components.stream().map(c -> this.buildDetailResponse(c, msgTypeMap)).toList();

    return buildPagedResponse(componentsResponse, pageResult.getPageInfo());
  }

  private DetailPmhComponentResponse buildDetailResponse(
      PmhComponents1 component, Map<String, MsgTypeResponse> msgTypeResponseMap) {
    DetailPmhComponentResponse detailResponse =
        pmhComponentMapper.toDetailComponentResponse(component);

    detailResponse.setStatus(
        ComponentStatusResponse.builder()
            .label(ComponentStatus.getLabelByValue(component.getStatus()))
            .value(component.getStatus())
            .build());
    detailResponse.setMessageType(msgTypeResponseMap.get(component.getMessageType()));

    return detailResponse;
  }

  private SearchPmhComponentResponse buildPagedResponse(
      List<DetailPmhComponentResponse> detailPmhComponentResponses, PageInfo pageInfo) {
    return SearchPmhComponentResponse.builder()
        .components(detailPmhComponentResponses)
        .page(pageInfo.getPage())
        .size(pageInfo.getSize())
        .totalElements(pageInfo.getTotalElements())
        .totalPages(pageInfo.getTotalPages())
        .build();
  }
}
