package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.model.Booking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Period;

/**
 * @author Igor Baiborodine
 */
public class BookingMaximumStayValidator
    implements ConstraintValidator<BookingMaximumStay, Booking> {

  @Override
  public void initialize(BookingMaximumStay constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= 3;
  }
}
