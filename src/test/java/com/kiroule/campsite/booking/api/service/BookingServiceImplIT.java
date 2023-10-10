package com.kiroule.campsite.booking.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.BaseIT;
import com.kiroule.campsite.booking.api.TestDataHelper;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
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

  @Autowired TestDataHelper testDataHelper;

  @Nested
  class CancelBooking {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsite = testDataHelper.createCampsiteEntity();
      BookingEntity booking = testDataHelper.createBookingEntity(campsite.getId());
      // when
      boolean cancelled = classUnderTest.cancelBooking(booking.getUuid());
      // then
      assertThat(cancelled).isTrue();
    }
  }
}
