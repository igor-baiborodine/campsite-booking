package com.kiroule.campsitebooking.model.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsitebooking.TestHelper;
import com.kiroule.campsitebooking.model.Booking;
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
 * Unit tests for {@link BookingMaximumStayValidator}.
 *
 * @author Igor Baiborodine
 */
public class BookingMaximumStayValidatorTest {

  private TestHelper helper = new TestHelper();

  private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  private Validator validator;

  @Before
  public void setUp() {
    validator = factory.getValidator();
  }

  @Test
  public void isValid_bookingWithThreeDayStay_noValidationErrors() {
    // given
    Booking booking = helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
    // when
    Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
    // then
    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  public void isValid_bookingWithFourDayStay_bookingMaximumStayValidationErrorThrown() {
    // given
    Booking booking = helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
    // when
    Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
    // then
    assertThat(violations.size()).isEqualTo(1);
    ConstraintViolation<Booking> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(BookingMaximumStay.class.getCanonicalName());
  }

}