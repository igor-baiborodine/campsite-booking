package com.kiroule.campsite.booking.api.model.validator;

import com.kiroule.campsite.booking.api.contract.v2.model.BookingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BookingAllowedStartDateValidator
    implements ConstraintValidator<BookingAllowedStartDate, BookingDto> {

  @Override
  public void initialize(BookingAllowedStartDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(
      BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return LocalDate.now().isBefore(booking.getStartDate())
        && booking.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
  }
}
