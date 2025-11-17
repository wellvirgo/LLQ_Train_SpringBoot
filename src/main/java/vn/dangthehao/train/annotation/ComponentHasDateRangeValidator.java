package vn.dangthehao.train.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.dangthehao.train.dto.component.request.HasDateRange;

import java.time.LocalDate;

public class ComponentHasDateRangeValidator
    implements ConstraintValidator<ComponentDateRange, HasDateRange> {
  @Override
  public boolean isValid(HasDateRange dateRange, ConstraintValidatorContext context) {
    LocalDate startDate = dateRange.getStartDate();
    LocalDate endDate = dateRange.getEndDate();
    if (startDate == null || endDate == null) {
      return true;
    }

    return !startDate.isAfter(endDate);
  }
}
