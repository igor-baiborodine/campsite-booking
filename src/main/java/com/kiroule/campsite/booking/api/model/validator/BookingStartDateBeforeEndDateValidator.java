package com.kiroule.campsite.booking.api.model.validator;

import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Igor Baiborodine
 */
public class BookingStartDateBeforeEndDateValidator implements
    ConstraintValidator<BookingStartDateBeforeEndDate, BookingDto> {

  @Override
  public void initialize(BookingStartDateBeforeEndDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return booking.getStartDate().isBefore(booking.getEndDate());
  }
}
