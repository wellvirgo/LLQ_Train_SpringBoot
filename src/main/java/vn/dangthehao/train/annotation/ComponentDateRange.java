package vn.dangthehao.train.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = {ComponentHasDateRangeValidator.class})
public @interface ComponentDateRange {
  String message() default
      "Effective date must be in the future and must not be later than the end effective date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
