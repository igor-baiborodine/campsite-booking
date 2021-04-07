package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@SpringBootTest
@ActiveProfiles("h2")
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class BookingServiceImplTestIT {

  @Autowired
  BookingService bookingService;

  @Autowired
  BookingRepository bookingRepository;

  UUID uuid;
  Booking existingBooking;
  boolean bookingCanceled;

  @BeforeEach
  void beforeEach() {
    bookingRepository.deleteAll();
    uuid = UUID.randomUUID();
    existingBooking = null;
    bookingCanceled = false;
  }

  @Test
  void cancel_booking__given_existing_active_booking__then_booking_canceled() {
    givenExistingActiveBooking(1, 2);

    whenCancelBooking();

    thenAssertBookingCanceled();
  }

  private void givenExistingActiveBooking(int startPlusDays, int endPlusDays) {
    Booking booking = buildBooking(
        LocalDate.now().plusDays(startPlusDays), LocalDate.now().plusDays(endPlusDays), uuid);
    existingBooking = bookingRepository.save(booking);
    assumeThat(existingBooking.isNew()).isFalse();
    assumeThat(existingBooking.isActive()).isTrue();
  }

  private void whenCancelBooking() {
    bookingCanceled = bookingService.cancelBooking(existingBooking.getUuid());
  }

  private void thenAssertBookingCanceled() {
    assertThat(bookingCanceled).isTrue();
  }
}
