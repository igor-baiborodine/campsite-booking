package com.kiroule.campsite.booking.api.model.validator;

import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BookingMaximumStayValidator}.
 *
 * @author Igor Baiborodine
 */
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class BookingValidatorTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator;
  LocalDate now;
  BookingDto bookingDto;
  Set<ConstraintViolation<BookingDto>> violations;

  @BeforeEach
  void beforeEach() {
    validator = factory.getValidator();
    now = LocalDate.now();
    bookingDto = null;
    violations = null;
  }

  @Nested
  class BookingAllowedStartDateValidator {

    @Test
    void given_booking_start_date_is_tomorrow__then_no_validation_errors_thrown() {
      givenBookingDto(now.plusDays(1), now.plusDays(4));

      whenValidateBooking();

      thenAssertNoValidationErrorsThrown();
    }

    @Test
    void given_booking_start_date_1_month_a_head__then_no_validation_errors_thrown() {
      givenBookingDto(now.plusMonths(1), now.plusMonths(1).plusDays(3));

      whenValidateBooking();

      thenAssertNoValidationErrorsThrown();
    }

    @Test
    void given_booking_start_date_1_month_and_1_day_ahead__then_BookingAllowedStartSate_error_thrown() {
      givenBookingDto(now.plusMonths(1).plusDays(1), now.plusMonths(1).plusDays(3));

      whenValidateBooking();

      thenAssertValidationErrorsThrown(BookingAllowedStartDate.class);
    }
  }

  @Nested
  class BookingStartDateBeforeEndDateValidator {

    @Test
    void given_start_date_is_before_end_date__then_no_validation_errors_thrown() {
      givenBookingDto(now.plusDays(1), now.plusDays(2));

      whenValidateBooking();

      thenAssertNoValidationErrorsThrown();
    }

    @Test
    void given_start_date_after_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      givenBookingDto(now.plusDays(2), now.plusDays(1));

      whenValidateBooking();

      thenAssertValidationErrorsThrown(BookingStartDateBeforeEndDate.class);
    }

    @Test
    void given_start_date_equals_to_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      givenBookingDto(now.plusDays(1), now.plusDays(1));

      whenValidateBooking();

      thenAssertValidationErrorsThrown(BookingStartDateBeforeEndDate.class);
    }
  }

  @Nested
  class BookingMaximumStayValidator {

    @Test
    void given_booking_with_three_day_stay__then_no_validation_errors() {
      givenBookingDto(now.plusDays(1), now.plusDays(4));

      whenValidateBooking();

      thenAssertNoValidationErrorsThrown();
    }

    @Test
    void given_booking_with_four_day_stay__then_BookingMaximumStay_error_thrown() {
      givenBookingDto(now.plusDays(1), now.plusDays(5));

      whenValidateBooking();

      thenAssertValidationErrorsThrown(BookingMaximumStay.class);
    }
  }

  private void givenBookingDto(LocalDate startDate, LocalDate endDate) {
    bookingDto = buildBookingDto(startDate, endDate);
  }

  private void whenValidateBooking() {
    violations = validator.validate(bookingDto);
  }

  private void thenAssertNoValidationErrorsThrown() {
    assertThat(violations.size()).isZero();
  }

  private void thenAssertValidationErrorsThrown(Class<?> constraint) {
    assertThat(violations.size()).isEqualTo(1);

    ConstraintViolation<BookingDto> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(constraint.getCanonicalName());
  }
}