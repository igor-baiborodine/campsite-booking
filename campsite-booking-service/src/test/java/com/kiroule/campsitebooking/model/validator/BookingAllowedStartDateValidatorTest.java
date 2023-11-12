package com.kiroule.campsitebooking.model.validator;

import static com.kiroule.campsitebooking.TestDataHelper.nextBooking;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsitebooking.model.Booking;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BookingAllowedStartDateValidatorTest {

  BookingAllowedStartDateValidator classUnderTest = new BookingAllowedStartDateValidator();

  @Nested
  class IsValid {

    @Test
    void happy_path() {
      // given
      Booking bookingDto = nextBooking();
      // when
      boolean result = classUnderTest.isValid(bookingDto, null);
      // then
      assertThat(result).isTrue();
    }
  }
}
