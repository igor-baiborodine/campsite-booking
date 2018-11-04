package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.model.Booking;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Igor Baiborodine
 */
public class BookingStartDateBeforeEndDateValidator implements
    ConstraintValidator<BookingStartDateBeforeEndDate, Booking> {

  @Override
  public void initialize(BookingStartDateBeforeEndDate constraintAnnotation) {

  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    if (booking == null) {
      return true;
    }
    return booking.getStartDate().isBefore(booking.getEndDate());
  }
}
