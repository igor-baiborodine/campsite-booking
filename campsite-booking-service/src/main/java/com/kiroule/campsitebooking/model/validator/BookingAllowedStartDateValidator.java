package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.model.Booking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BookingAllowedStartDateValidator
    implements ConstraintValidator<BookingAllowedStartDate, Booking> {

  @Override
  public void initialize(BookingAllowedStartDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    return LocalDate.now().isBefore(booking.getStartDate())
        && booking.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
  }
}
