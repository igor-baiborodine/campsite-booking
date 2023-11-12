package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.model.Booking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Igor Baiborodine
 */
public class BookingStartDateBeforeEndDateValidator
    implements ConstraintValidator<BookingStartDateBeforeEndDate, Booking> {

  @Override
  public void initialize(BookingStartDateBeforeEndDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    return booking.getStartDate().isBefore(booking.getEndDate());
  }
}
