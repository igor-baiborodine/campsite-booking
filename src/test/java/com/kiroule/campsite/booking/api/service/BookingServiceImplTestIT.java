package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.kiroule.campsite.booking.api.BaseTestIT;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
class BookingServiceImplTestIT extends BaseTestIT {

  @Autowired @Qualifier("bookingService")
  BookingService classUnderTest;

  @Autowired
  BookingRepository bookingRepository;

  UUID uuid;
  Booking existingBooking;

  @BeforeEach
  void beforeEach() {
    bookingRepository.deleteAll();

    uuid = UUID.randomUUID();
    existingBooking = null;
  }

  @Nested
  class CancelBooking {

    boolean bookingCanceled;

    @Test
    void happy_path() {
      given_existingActiveBooking(1, 2);

      when_cancelBooking();

      then_assertBookingCanceled();
    }

    private void given_existingActiveBooking(int startPlusDays, int endPlusDays) {
      Booking booking = buildBooking(now().plusDays(startPlusDays), now().plusDays(endPlusDays), uuid);
      existingBooking = bookingRepository.save(booking);
      assumeThat(existingBooking.isNew()).isFalse();
      assumeThat(existingBooking.isActive()).isTrue();
    }

    private void when_cancelBooking() {
      bookingCanceled = classUnderTest.cancelBooking(existingBooking.getUuid());
    }

    private void then_assertBookingCanceled() {
      assertThat(bookingCanceled).isTrue();
    }
  }
}
