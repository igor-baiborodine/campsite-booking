package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.model.Booking;
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

  }

  @Override
  public boolean isValid(Booking booking, ConstraintValidatorContext constraintValidatorContext) {
    if (booking == null) {
      return true;
    }

    return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= 3;
  }
}
