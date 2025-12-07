package vn.dangthehao.train.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import vn.dangthehao.train.dto.component.request.SearchPmhComponentRequest;
import vn.dangthehao.train.entity.PmhComponents1;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PmhComponentSpecs {
  public static Specification<PmhComponents1> buildSpec(SearchPmhComponentRequest searchRequest) {
    String componentCode = searchRequest.getComponentCode();
    String componentName = searchRequest.getComponentName();
    String messageType = searchRequest.getMessageType();
    String connectionMethod = searchRequest.getConnectionMethod();
    String checkToken = searchRequest.getCheckToken();
    Long isDisplay = searchRequest.getIsDisplay();
    Long status = searchRequest.getStatus();
    Long isActive = searchRequest.getIsActive();
    LocalDate effectiveDateFrom = searchRequest.getEffectiveDateFrom();
    LocalDate effectiveDateTo = searchRequest.getEffectiveDateTo();
    LocalDate endEffectiveDateFrom = searchRequest.getEndEffectiveDateFrom();
    LocalDate endEffectiveDateTo = searchRequest.getEndEffectiveDateTo();

    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (isNotNullOrBlank(componentCode)) {
        predicates.add(equal(root, cb, "componentCode", componentCode.toLowerCase().trim()));
      }

      if (isNotNullOrBlank(componentName)) {
        predicates.add(like(root, cb, "componentName", componentName.toLowerCase().trim()));
      }

      if (isNotNullOrBlank(messageType)) {
        predicates.add(like(root, cb, "messageType", messageType.toLowerCase().trim()));
      }

      if (isNotNullOrBlank(connectionMethod)) {
        predicates.add(like(root, cb, "connectionMethod", connectionMethod.toLowerCase().trim()));
      }

      if (isNotNullOrBlank(checkToken)) {
        predicates.add(equal(root, cb, "checkToken", checkToken.toLowerCase().trim()));
      }

      if (isNotNull(isDisplay)) {
        predicates.add(equal(root, cb, "isDisplay", isDisplay));
      }

      if (isNotNull(status)) {
        predicates.add(equal(root, cb, "status", status));
      }

      if (isNotNull(isActive)) {
        predicates.add(equal(root, cb, "isActive", isActive));
      }

      if (isNotNull(effectiveDateFrom)) {
        predicates.add(from(root, cb, "effectiveDate", effectiveDateFrom));
      }

      if (isNotNull(effectiveDateTo)) {
        predicates.add(to(root, cb, "effectiveDate", effectiveDateTo));
      }

      if (isNotNull(endEffectiveDateFrom)) {
        predicates.add(from(root, cb, "endEffectiveDate", endEffectiveDateFrom));
      }

      if (isNotNull(endEffectiveDateTo)) {
        predicates.add(to(root, cb, "endEffectiveDate", endEffectiveDateTo));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private static Predicate equal(
      Root<PmhComponents1> root, CriteriaBuilder cb, String field, Long value) {
    return cb.equal(root.get(field), value);
  }

  // For String, compare case-insensitively
  private static Predicate equal(
      Root<PmhComponents1> root, CriteriaBuilder cb, String field, String value) {
    return cb.equal(cb.lower(root.get(field)), value.toLowerCase());
  }

  // Compare case-insensitively
  private static Predicate like(
      Root<PmhComponents1> root, CriteriaBuilder cb, String field, String value) {
    value = ("%" + value.trim() + "%").toLowerCase();
    return cb.like(cb.lower(root.get(field)), value);
  }

  private static Predicate from(
      Root<PmhComponents1> root, CriteriaBuilder cb, String field, LocalDate value) {
    return cb.greaterThanOrEqualTo(root.get(field), value);
  }

  private static Predicate to(
      Root<PmhComponents1> root, CriteriaBuilder cb, String field, LocalDate value) {
    return cb.lessThanOrEqualTo(root.get(field), value);
  }

  private static boolean isNotNullOrBlank(String value) {
    return value != null && !value.isBlank();
  }

  private static boolean isNotNull(Object value) {
    return value != null;
  }
}
