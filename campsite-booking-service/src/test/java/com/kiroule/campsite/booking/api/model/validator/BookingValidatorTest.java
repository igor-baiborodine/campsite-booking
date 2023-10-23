package com.kiroule.campsite.booking.api.model.validator;

import static com.kiroule.campsite.booking.api.TestDataHelper.nextBookingDto;
import static java.time.LocalDate.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link BookingMaximumStayValidator}.
 *
 * @author Igor Baiborodine
 */
class BookingValidatorTest {

  Validator classUnderTest = Validation.buildDefaultValidatorFactory().getValidator();

  @Nested
  class BookingAllowedStartDateValidator {

    @Test
    void happy_path() {
      // given
      BookingDto bookingDto = nextBookingDto();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertThat(violations.size()).isZero();
    }

    @Test
    void given_booking_start_date_1_month_a_head__then_no_validation_errors_thrown() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .startDate(now.plusMonths(1))
              .endDate(now.plusMonths(1).plusDays(3))
              .build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertThat(violations.size()).isZero();
    }

    @Test
    void
        given_booking_start_date_1_month_and_1_day_ahead__then_BookingAllowedStartDate_error_thrown() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .startDate(now.plusMonths(1).plusDays(1))
              .endDate(now.plusMonths(1).plusDays(3))
              .build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertValidationErrors(violations, BookingAllowedStartDate.class);
    }
  }

  @Nested
  class BookingStartDateBeforeEndDateValidator {

    @Test
    void happy_path() {
      // given
      BookingDto bookingDto = nextBookingDto();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertThat(violations.size()).isZero();
    }

    @Test
    void given_start_date_after_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder().startDate(now.plusDays(2)).endDate(now.plusDays(1)).build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertValidationErrors(violations, BookingStartDateBeforeEndDate.class);
    }

    @Test
    void given_start_date_equals_to_end_date__then_BookingStartDateBeforeEndDate_error_thrown() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder().startDate(now.plusDays(1)).endDate(now.plusDays(1)).build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertValidationErrors(violations, BookingStartDateBeforeEndDate.class);
    }
  }

  @Nested
  class BookingMaximumStayValidator {

    @Test
    void happy_path() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder().startDate(now.plusDays(1)).endDate(now.plusDays(4)).build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertThat(violations.size()).isZero();
    }

    @Test
    void given_booking_with_four_day_stay__then_BookingMaximumStay_error_thrown() {
      // given
      LocalDate now = now();
      BookingDto bookingDto =
          nextBookingDto().toBuilder().startDate(now.plusDays(1)).endDate(now.plusDays(5)).build();
      // when
      Set<ConstraintViolation<BookingDto>> violations = classUnderTest.validate(bookingDto);
      // then
      assertValidationErrors(violations, BookingMaximumStay.class);
    }
  }

  private void assertValidationErrors(
      Set<ConstraintViolation<BookingDto>> violations, Class<?> constraint) {
    assertThat(violations.size()).isEqualTo(1);

    ConstraintViolation<BookingDto> violation = violations.iterator().next();
    Annotation annotation = violation.getConstraintDescriptor().getAnnotation();
    assertThat(annotation.annotationType().getCanonicalName())
        .isEqualTo(constraint.getCanonicalName());
  }
}
