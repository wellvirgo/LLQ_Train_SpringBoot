package vn.dangthehao.train.dto.component.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import vn.dangthehao.train.dto.common.PageableRequest;
import vn.dangthehao.train.enums.SearchTech;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchPmhComponentRequest extends PageableRequest {
  static final SearchTech DEFAULT_SEARCH_TECH = SearchTech.JPA_SPECIFICATION;

  String componentCode;

  String componentName;

  String messageType;

  String connectionMethod;

  String checkToken;

  Long isDisplay;

  Long status;

  Long isActive;

  LocalDate effectiveDateFrom;

  LocalDate effectiveDateTo;

  LocalDate endEffectiveDateFrom;

  LocalDate endEffectiveDateTo;

  SearchTech searchTech;

  public SearchTech getSearchTechOrDefault() {
    if (this.getSearchTech() == null) return DEFAULT_SEARCH_TECH;
    return this.getSearchTech();
  }
}
