package com.kiroule.campsitebooking.service;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsitebooking.BaseIT;
import com.kiroule.campsitebooking.TestDataHelper;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.entity.BookingEntity;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
class BookingServiceImplIT extends BaseIT {

  @Autowired
  @Qualifier("bookingService")
  BookingService classUnderTest;

  @Autowired
  TestDataHelper testDataHelper;

  @Nested
  class FindByUuid {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      // when
      Booking result = classUnderTest.findByUuid(bookingEntity.getUuid());
      // then
      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields("createdAt", "updatedAt")
          .isEqualTo(bookingEntity);
      assertThat(result.getCreatedAt().truncatedTo(MILLIS))
          .isEqualTo(bookingEntity.getCreatedAt().truncatedTo(MILLIS));
      assertThat(result.getUpdatedAt().truncatedTo(MILLIS))
          .isEqualTo(bookingEntity.getUpdatedAt().truncatedTo(MILLIS));
    }
  }

  @Nested
  class CancelBooking {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      // when
      boolean result = classUnderTest.cancelBooking(bookingEntity.getUuid());
      // then
      assertThat(result).isTrue();
    }
  }
}
