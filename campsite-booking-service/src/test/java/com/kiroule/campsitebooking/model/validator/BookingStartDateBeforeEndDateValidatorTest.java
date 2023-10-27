package com.kiroule.campsitebooking.model.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.kiroule.campsitebooking.TestDataHelper;
import com.kiroule.campsitebooking.contract.v2.dto.BookingDto;
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
      BookingDto bookingDto = TestDataHelper.nextBookingDto();
      // when
      boolean result = classUnderTest.isValid(bookingDto, null);
      // then
      assertThat(result).isTrue();
    }
  }
}
