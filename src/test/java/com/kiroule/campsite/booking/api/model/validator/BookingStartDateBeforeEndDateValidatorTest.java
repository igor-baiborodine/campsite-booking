package com.kiroule.campsite.booking.api.model.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import com.kiroule.campsite.booking.api.TestHelper;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link BookingStartDateBeforeEndDateValidator}.
 *
 * @author Igor Baiborodine
 */
public class BookingStartDateBeforeEndDateValidatorTest {

  private TestHelper helper = new TestHelper();

  private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @Before
  public void setUp() {
    validator = factory.getValidator();
  }

  @Test
  public void isValid_startDateBeforeToEndDate_noValidationErrors() {
    // given
    BookingDto bookingDto = helper.buildBooking(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    // when
    Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    // then
    assertThat(violations.size()).isZero();
  }

  @Test
  public void isValid_startDateAfterEndDate_bookingStartDateBeforeEndDateValidationErrorThrown() {
    // given
    BookingDto bookingDto = helper.buildBooking(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1));
    // when
    Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    // then
    assertThat(violations.size()).isEqualTo(1);
    ConstraintViolation<BookingDto> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(BookingStartDateBeforeEndDate.class.getCanonicalName());
  }

  @Test
  public void isValid_startDateEqualsToEndDate_bookingStartDateBeforeEndDateValidationErrorThrown() {
    // given
    BookingDto bookingDto = helper.buildBooking(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));
    // when
    Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
    // then
    assertThat(violations.size()).isEqualTo(1);
    ConstraintViolation<BookingDto> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(BookingStartDateBeforeEndDate.class.getCanonicalName());
  }

}