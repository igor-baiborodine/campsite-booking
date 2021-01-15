package com.kiroule.campsitebooking.api.model.validator;

import com.kiroule.campsitebooking.api.contract.v1.model.BookingDto;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingAllowedStartDateValidator
    implements ConstraintValidator<BookingAllowedStartDate, BookingDto> {

  @Override
  public void initialize(BookingAllowedStartDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return LocalDate.now().isBefore(booking.getStartDate())
        && booking.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
  }
}
