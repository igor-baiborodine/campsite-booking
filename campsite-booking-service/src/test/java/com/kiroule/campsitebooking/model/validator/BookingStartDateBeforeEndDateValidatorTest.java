package com.kiroule.campsitebooking.model.validator;

import static com.kiroule.campsitebooking.TestDataHelper.nextBooking;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsitebooking.model.Booking;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BookingStartDateBeforeEndDateValidatorTest {

  BookingStartDateBeforeEndDateValidator classUnderTest =
      new BookingStartDateBeforeEndDateValidator();

  @Nested
  class IsValid {

    @Test
    void happy_path() {
      // given
      Booking Booking = nextBooking();
      // when
      boolean result = classUnderTest.isValid(Booking, null);
      // then
      assertThat(result).isTrue();
    }
  }
}
