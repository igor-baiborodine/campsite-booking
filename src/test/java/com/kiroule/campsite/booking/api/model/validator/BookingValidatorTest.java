package com.kiroule.campsite.booking.api.model.validator;

import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BookingMaximumStayValidator}.
 *
 * @author Igor Baiborodine
 */
class BookingValidatorTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator classUnderTest;
  LocalDate now;
  BookingDto bookingDto;
  Set<ConstraintViolation<BookingDto>> violations;

  @BeforeEach
  void beforeEach() {
    classUnderTest = factory.getValidator();
    now = LocalDate.now();
    bookingDto = null;
    violations = null;
  }

  private void given_bookingDto(LocalDate startDate, LocalDate endDate) {
    bookingDto = buildBookingDto(startDate, endDate);
  }

  private void when_validateBooking() {
    violations = classUnderTest.validate(bookingDto);
  }

  private void then_assertNoValidationErrorsThrown() {
    assertThat(violations.size()).isZero();
  }

  private void then_assertValidationErrorsThrown(Class<?> constraint) {
    assertThat(violations.size()).isEqualTo(1);

    ConstraintViolation<BookingDto> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(constraint.getCanonicalName());
  }

  @Nested
  class BookingAllowedStartDateValidator {

    @Test
    void given_booking_start_date_is_tomorrow__then_no_validation_errors_thrown() {
      given_bookingDto(now.plusDays(1), now.plusDays(4));

      when_validateBooking();

      then_assertNoValidationErrorsThrown();
    }

    @Test
    void given_booking_start_date_1_month_a_head__then_no_validation_errors_thrown() {
      given_bookingDto(now.plusMonths(1), now.plusMonths(1).plusDays(3));

      when_validateBooking();

      then_assertNoValidationErrorsThrown();
    }

    @Test
    void
        given_booking_start_date_1_month_and_1_day_ahead__then_BookingAllowedStartSate_error_thrown() {
      given_bookingDto(now.plusMonths(1).plusDays(1), now.plusMonths(1).plusDays(3));

      when_validateBooking();

      then_assertValidationErrorsThrown(BookingAllowedStartDate.class);
    }
  }

  @Nested
  class BookingStartDateBeforeEndDateValidator {

    @Test
    void given_start_date_is_before_end_date__then_no_validation_errors_thrown() {
      given_bookingDto(now.plusDays(1), now.plusDays(2));

      when_validateBooking();

      then_assertNoValidationErrorsThrown();
    }

    @Test
    void given_start_date_after_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      given_bookingDto(now.plusDays(2), now.plusDays(1));

      when_validateBooking();

      then_assertValidationErrorsThrown(BookingStartDateBeforeEndDate.class);
    }

    @Test
    void given_start_date_equals_to_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      given_bookingDto(now.plusDays(1), now.plusDays(1));

      when_validateBooking();

      then_assertValidationErrorsThrown(BookingStartDateBeforeEndDate.class);
    }
  }

  @Nested
  class BookingMaximumStayValidator {

    @Test
    void given_booking_with_three_day_stay__then_no_validation_errors() {
      given_bookingDto(now.plusDays(1), now.plusDays(4));

      when_validateBooking();

      then_assertNoValidationErrorsThrown();
    }

    @Test
    void given_booking_with_four_day_stay__then_BookingMaximumStay_error_thrown() {
      given_bookingDto(now.plusDays(1), now.plusDays(5));

      when_validateBooking();

      then_assertValidationErrorsThrown(BookingMaximumStay.class);
    }
  }
}
