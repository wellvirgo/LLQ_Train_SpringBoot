package vn.dangthehao.train.repository;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import vn.dangthehao.train.dto.common.PageResult;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;
import vn.dangthehao.train.enums.ComponentSortField;
import vn.dangthehao.train.util.PageResultBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Repository
public class PmhComponents1CustomRepositoryImpl implements PmhComponents1CustomRepository {
  EntityManager entityManager;

  static final String WHERE_CLAUSE = " WHERE 1=1";

  @Override
  public PageResult<PmhComponents1> findAllUseEntityManger(
      SearchPmhComponentRequest searchRequest) {
    StringBuilder nativeSql =
        new StringBuilder("SELECT * FROM PMH_COMPONENTS_1").append(WHERE_CLAUSE);
    Query query = buildNativeQuery(PmhComponents1.class, searchRequest, nativeSql);

    return getPageResult(query, searchRequest);
  }

  @Override
  public long count(SearchPmhComponentRequest searchRequest) {
    StringBuilder sql =
        new StringBuilder("SELECT COUNT(*) FROM PMH_COMPONENTS_1").append(WHERE_CLAUSE);
    Query query = buildNativeQuery(Long.class, searchRequest, sql);

    return (Long) query.getSingleResult();
  }

  @Override
  public PageResult<PmhComponents1> findAllUseProcedure(SearchPmhComponentRequest searchRequest) {
    final String PROCEDURE_NAME = "SEARCH_COMPONENTS_1";
    StoredProcedureQuery query =
        entityManager.createStoredProcedureQuery(PROCEDURE_NAME, PmhComponents1.class);

    setInParameters(query, searchRequest);
    setOtParameters(query);
    query.execute();

    return getPageResult(query);
  }

  /* Helper method for Entity Manager and Native Query */
  private static boolean isNotNullOrBlank(String value) {
    return value != null && !value.isBlank();
  }

  private static boolean isNotNull(Object value) {
    return value != null;
  }

  private void setOrderClause(StringBuilder sql, SearchPmhComponentRequest searchRequest) {
    sql.append(" ORDER BY ");
    sql.append(searchRequest.getSortFieldOrDefault().dbColumn()).append(", ");
    sql.append(ComponentSortField.ID.dbColumn()).append(" ");
    sql.append(searchRequest.getSortDirectionOrDefault());
  }

  private void buildSql(
      SearchPmhComponentRequest searchRequest, StringBuilder sql, Map<String, Object> parameters) {
    if (isNotNullOrBlank(searchRequest.getComponentCode())) {
      sql.append(" AND LOWER(COMPONENT_CODE) = :componentCode");
      parameters.put("componentCode", searchRequest.getComponentCode().toLowerCase().trim());
    }

    if (isNotNullOrBlank(searchRequest.getComponentName())) {
      sql.append(" AND LOWER(COMPONENT_NAME) like :componentName");
      parameters.put("componentName", "%" + searchRequest.getComponentName().toLowerCase().trim() + "%");
    }

    if (isNotNullOrBlank(searchRequest.getMessageType())) {
      sql.append(" AND LOWER(MESSAGE_TYPE) like :messageType");
      parameters.put("messageType", "%" + searchRequest.getMessageType().toLowerCase().trim() + "%");
    }

    if (isNotNullOrBlank(searchRequest.getConnectionMethod())) {
      sql.append(" AND LOWER(CONNECTION_METHOD) like :connectionMethod");
      parameters.put(
          "connectionMethod", "%" + searchRequest.getConnectionMethod().toLowerCase().trim() + "%");
    }

    if (isNotNullOrBlank(searchRequest.getCheckToken())) {
      sql.append(" AND LOWER(CHECK_TOKEN) = :checkToken");
      parameters.put("checkToken", searchRequest.getCheckToken().toLowerCase().trim());
    }

    if (isNotNull(searchRequest.getIsDisplay())) {
      sql.append(" AND IS_DISPLAY = :isDisplay");
      parameters.put("isDisplay", searchRequest.getIsDisplay());
    }

    if (isNotNull(searchRequest.getStatus())) {
      sql.append(" AND STATUS = :status");
      parameters.put("status", searchRequest.getStatus());
    }

    if (isNotNull(searchRequest.getIsActive())) {
      sql.append(" AND IS_ACTIVE = :isActive");
      parameters.put("isActive", searchRequest.getIsActive());
    }

    if (isNotNull(searchRequest.getEffectiveDateFrom())) {
      sql.append(" AND EFFECTIVE_DATE >= :effectiveDateFrom");
      parameters.put("effectiveDateFrom", searchRequest.getEffectiveDateFrom());
    }

    if (isNotNull(searchRequest.getEffectiveDateTo())) {
      sql.append(" AND EFFECTIVE_DATE <= :effectiveDateTo");
      parameters.put("effectiveDateTo", searchRequest.getEffectiveDateTo());
    }

    if (isNotNull(searchRequest.getEndEffectiveDateFrom())) {
      sql.append(" AND END_EFFECTIVE_DATE >= :endEffectiveDateFrom");
      parameters.put("endEffectiveDateFrom", searchRequest.getEndEffectiveDateFrom());
    }

    if (isNotNull(searchRequest.getEndEffectiveDateTo())) {
      sql.append(" AND END_EFFECTIVE_DATE <= :endEffectiveDateTo");
      parameters.put("endEffectiveDateTo", searchRequest.getEndEffectiveDateTo());
    }

    setOrderClause(sql, searchRequest);
  }

  private <T> Query buildNativeQuery(
      Class<T> entityClass, SearchPmhComponentRequest searchRequest, StringBuilder sql) {
    Map<String, Object> parameters = new HashMap<>();
    buildSql(searchRequest, sql, parameters);

    Query query = entityManager.createNativeQuery(sql.toString(), entityClass);
    parameters.forEach(query::setParameter);

    return query;
  }

  private PageResult<PmhComponents1> getPageResult(
      Query query, SearchPmhComponentRequest searchRequest) {
    int requestedPage = searchRequest.getPageOrDefault();
    int requestedSize = searchRequest.getSizeOrDefault();

    int startPos = (requestedPage - 1) * requestedSize;
    query.setFirstResult(startPos);
    query.setMaxResults(requestedSize);
    List<PmhComponents1> resultList = (List<PmhComponents1>) query.getResultList();

    long totalElements = count(searchRequest);
    int totalPages = (int) Math.ceil((double) totalElements / requestedSize);

    return PageResultBuilder.build(
        resultList, requestedPage, resultList.size(), totalElements, totalPages);
  }

  /* Helper method for Stored Procedure */
  private void setInParameters(
      StoredProcedureQuery query, SearchPmhComponentRequest searchRequest) {
    String pComponentCode = "p_component_code";
    String pComponentName = "p_component_name";
    String pMessageType = "p_message_type";
    String pConnectionMethod = "p_connection_method";
    String pCheckToken = "p_check_token";
    String pIsDisplay = "p_is_display";
    String pStatus = "p_status";
    String pIsActive = "p_is_active";
    String pEffectiveDateFrom = "p_effective_date_from";
    String pEffectiveDateTo = "p_effective_date_to";
    String pEndEffectiveDateFrom = "p_end_effective_date_from";
    String pEndEffectiveDateTo = "p_end_effective_date_to";
    String pPage = "p_page";
    String pSize = "p_size";
    String pOrderBy = "p_order_by";
    String pSortDirection = "p_order_direction";

    // Register INPUT
    query.registerStoredProcedureParameter(pComponentCode, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pComponentName, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pMessageType, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pConnectionMethod, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pCheckToken, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pIsDisplay, Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pStatus, Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pIsActive, Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pEffectiveDateFrom, LocalDate.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pEffectiveDateTo, LocalDate.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(
        pEndEffectiveDateFrom, LocalDate.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pEndEffectiveDateTo, LocalDate.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pPage, Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pSize, Integer.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pOrderBy, String.class, ParameterMode.IN);
    query.registerStoredProcedureParameter(pSortDirection, String.class, ParameterMode.IN);

    // Set value for INPUT
    query.setParameter(pComponentCode, searchRequest.getComponentCode());
    query.setParameter(pComponentName, searchRequest.getComponentName());
    query.setParameter(pMessageType, searchRequest.getMessageType());
    query.setParameter(pConnectionMethod, searchRequest.getConnectionMethod());
    query.setParameter(pCheckToken, searchRequest.getCheckToken());
    query.setParameter(pIsDisplay, searchRequest.getIsDisplay());
    query.setParameter(pStatus, searchRequest.getStatus());
    query.setParameter(pIsActive, searchRequest.getIsActive());
    query.setParameter(pEffectiveDateFrom, searchRequest.getEffectiveDateFrom());
    query.setParameter(pEffectiveDateTo, searchRequest.getEffectiveDateTo());
    query.setParameter(pEndEffectiveDateFrom, searchRequest.getEndEffectiveDateFrom());
    query.setParameter(pEndEffectiveDateTo, searchRequest.getEndEffectiveDateTo());
    query.setParameter(pPage, searchRequest.getPageOrDefault());
    query.setParameter(pSize, searchRequest.getSizeOrDefault());
    query.setParameter(pOrderBy, searchRequest.getSortFieldOrDefault().dbColumn());
    query.setParameter(pSortDirection, searchRequest.getSortDirectionOrDefault().name());
  }

  private void setOtParameters(StoredProcedureQuery query) {
    // Register OUTPUT
    // Use void.class for SYS_REFCURSOR
    query.registerStoredProcedureParameter("p_result", void.class, ParameterMode.REF_CURSOR);
    query.registerStoredProcedureParameter("p_out_page", Integer.class, ParameterMode.OUT);
    query.registerStoredProcedureParameter("p_out_size", Integer.class, ParameterMode.OUT);
    query.registerStoredProcedureParameter("p_total_elements", Integer.class, ParameterMode.OUT);
    query.registerStoredProcedureParameter("p_total_pages", Integer.class, ParameterMode.OUT);
  }

  private PageResult<PmhComponents1> getPageResult(StoredProcedureQuery query) {
    List<PmhComponents1> result = query.getResultList();
    Integer page = (Integer) query.getOutputParameterValue("p_out_page");
    Integer size = (Integer) query.getOutputParameterValue("p_out_size");
    Long totalElements = Long.valueOf((Integer) query.getOutputParameterValue("p_total_elements"));
    Integer totalPages = (Integer) query.getOutputParameterValue("p_total_pages");

    return PageResultBuilder.build(result, page, size, totalElements, totalPages);
  }
}
