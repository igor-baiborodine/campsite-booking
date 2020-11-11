package com.kiroule.campsitebooking.api.model.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsitebooking.api.TestHelper;
import com.kiroule.campsitebooking.api.model.Booking;
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
    Booking booking = helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    // when
    Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
    // then
    assertThat(violations.size()).isZero();
  }

  @Test
  public void isValid_startDateAfterEndDate_bookingStartDateBeforeEndDateValidationErrorThrown() {
    // given
    Booking booking = helper.buildBooking(LocalDate.now().plusDays(2), LocalDate.now().plusDays(1));
    // when
    Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
    // then
    assertThat(violations.size()).isEqualTo(1);
    ConstraintViolation<Booking> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(BookingStartDateBeforeEndDate.class.getCanonicalName());
  }

  @Test
  public void isValid_startDateEqualsToEndDate_bookingStartDateBeforeEndDateValidationErrorThrown() {
    // given
    Booking booking = helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(1));
    // when
    Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
    // then
    assertThat(violations.size()).isEqualTo(1);
    ConstraintViolation<Booking> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(BookingStartDateBeforeEndDate.class.getCanonicalName());
  }

}