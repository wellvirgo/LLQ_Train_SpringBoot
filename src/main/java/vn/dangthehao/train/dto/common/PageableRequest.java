package vn.dangthehao.train.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;
import vn.dangthehao.train.enums.ComponentSortField;
import vn.dangthehao.train.enums.SortDirection;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageableRequest {
  static final Integer DEFAULT_PAGE_NUMBER = 1;
  static final Long DEFAULT_PAGE_SIZE = 20L;

  @Min(value = 1, message = "Page number must be greater than 0")
  Integer page;

  @Min(value = 0, message = "Page size must be at least 0")
  Long size;

  ComponentSortField sortField;
  SortDirection sortDirection;

  @JsonIgnore
  public Integer getPageOrDefault() {
    if (this.getPage() == null) return DEFAULT_PAGE_NUMBER;
    return this.getPage();
  }

  @JsonIgnore
  public Long getSizeOrDefault() {
    if (this.getSize() == null) return DEFAULT_PAGE_SIZE;
    return this.getSize();
  }

  @JsonIgnore
  public ComponentSortField getSortFieldOrDefault() {
    // default sort by ID
    if (sortField != null) return sortField;
    return ComponentSortField.ID;
  }

  @JsonIgnore
  public Sort.Direction getSortDirectionOrDefault() {
    if (sortDirection != null && sortDirection.equals(SortDirection.DESC))
      return Sort.Direction.DESC;
    return Sort.Direction.ASC;
  }
}
