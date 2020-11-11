package com.kiroule.campsitebooking.api.model.validator;

import com.kiroule.campsitebooking.api.model.Booking;
import java.time.LocalDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingAllowedStartDateValidator
    implements ConstraintValidator<BookingAllowedStartDate, Booking> {

  @Override
  public void initialize(BookingAllowedStartDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    if (booking == null) {
      return true;
    }
    return LocalDate.now().isBefore(booking.getStartDate())
        && booking.getStartDate().isBefore(LocalDate.now().plusMonths(1).plusDays(1));
  }
}
