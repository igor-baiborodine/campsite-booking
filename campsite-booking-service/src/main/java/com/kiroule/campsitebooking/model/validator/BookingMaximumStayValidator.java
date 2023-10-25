package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.contract.v2.dto.BookingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Period;

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
  public boolean isValid(
      BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return Period.between(booking.getStartDate(), booking.getEndDate()).getDays() <= 3;
  }
}
