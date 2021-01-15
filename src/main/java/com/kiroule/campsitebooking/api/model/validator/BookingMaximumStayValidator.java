package com.kiroule.campsitebooking.api.model.validator;

import com.kiroule.campsitebooking.api.contract.v1.model.BookingDto;
import java.time.Period;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Igor Baiborodine
 */
public class BookingMaximumStayValidator
    implements ConstraintValidator<BookingMaximumStay, BookingDto> {

  @Override
  public void initialize(BookingMaximumStay constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= 3;
  }
}
