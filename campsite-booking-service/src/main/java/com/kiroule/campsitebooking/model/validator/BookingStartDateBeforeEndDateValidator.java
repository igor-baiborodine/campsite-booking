package com.kiroule.campsitebooking.model.validator;

import com.kiroule.campsitebooking.contract.v2.dto.BookingDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Igor Baiborodine
 */
public class BookingStartDateBeforeEndDateValidator
    implements ConstraintValidator<BookingStartDateBeforeEndDate, BookingDto> {

  @Override
  public void initialize(BookingStartDateBeforeEndDate constraintAnnotation) {
    // no additional initialization needed
  }

  @Override
  public boolean isValid(
      BookingDto booking, ConstraintValidatorContext constraintValidatorContext) {
    return booking.getStartDate().isBefore(booking.getEndDate());
  }
}
