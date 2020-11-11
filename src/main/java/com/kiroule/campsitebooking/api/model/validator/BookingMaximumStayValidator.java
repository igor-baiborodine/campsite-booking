package com.kiroule.campsitebooking.api.model.validator;

import com.kiroule.campsitebooking.api.model.Booking;
import java.time.Period;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
    if (booking == null) {
      return true;
    }

    return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= 3;
  }
}
