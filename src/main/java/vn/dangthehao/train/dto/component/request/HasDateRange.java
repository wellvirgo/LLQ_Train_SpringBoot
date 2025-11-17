package vn.dangthehao.train.dto.component.request;

import java.time.LocalDate;

public interface HasDateRange {
  LocalDate getStartDate();

  LocalDate getEndDate();
}
