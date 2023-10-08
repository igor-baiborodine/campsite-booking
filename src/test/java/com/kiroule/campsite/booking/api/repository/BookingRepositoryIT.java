package com.kiroule.campsite.booking.api.repository;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.kiroule.campsite.booking.api.BaseIT;
import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
class BookingRepositoryIT extends BaseIT {

  @Autowired BookingRepository classUnderTest;

  @Autowired CampsiteRepository campsiteRepository;

  @Autowired BookingMapper bookingMapper;

  LocalDate now;
  UUID uuid;
  BookingEntity existingBookingEntity;

  @BeforeEach
  void beforeEach() {
    classUnderTest.deleteAll();

    now = now();
    uuid = randomUUID();
    existingBookingEntity = null;
  }

  private void given_existingBooking(int startPlusDays, int endPlusDays) {
    Booking booking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    existingBookingEntity = classUnderTest.save(bookingMapper.toBookingEntity(booking));
  }

  @Nested
  class FindByUuid {

    Optional<BookingEntity> bookingOptionalForUuid;

    @BeforeEach
    void beforeEach() {
      bookingOptionalForUuid = null;
    }

    @Test
    void happy_path() {
      given_existingBooking(1, 2);

      when_findByUuid();

      then_assertBookingFoundForUuid();
    }

    private void when_findByUuid() {
      bookingOptionalForUuid = classUnderTest.findByUuid(uuid);
    }

    private void then_assertBookingFoundForUuid() {
      assertAll(
          "foundBooking",
          () -> assertThat(bookingOptionalForUuid).hasValue(existingBookingEntity),
          () ->
              assertThat(bookingOptionalForUuid.get().getCreatedAt().toLocalDate()).isEqualTo(now));
    }
  }

  @Nested
  class FindForDateRange {

    List<BookingEntity> bookingsForDateRange;

    @BeforeEach
    void beforeEach() {
      bookingsForDateRange = null;
    }

    @Test
    @DisplayNamePrefix("SE|-|----|-|--")
    void given_booking_dates_before_range_start_date__then_no_booking_found() {
      given_existingBooking(1, 2);

      when_findBookingsForDateRange(3, 4);

      then_assertNoBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("-S|E|----|-|--")
    void
        given_booking_start_date_before_range_start_date_and_booking_end_date_equals_to_range_start_date__then_no_booking_found() {
      given_existingBooking(1, 2);

      when_findBookingsForDateRange(2, 3);

      then_assertNoBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("-S|-|E---|-|--")
    void
        given_booking_start_date_before_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
      given_existingBooking(1, 3);

      when_findBookingsForDateRange(2, 4);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|S|E---|-|--")
    void
        given_booking_start_date_equals_to_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
      given_existingBooking(1, 2);

      when_findBookingsForDateRange(1, 3);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|-|S--E|-|--")
    void given_booking_dates_within_range_dates__then_booking_found() {
      given_existingBooking(2, 3);

      when_findBookingsForDateRange(1, 4);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|-|---S|E|--")
    void
        given_booking_start_date_within_range_dates_and_booking_end_date_equals_to_range_end_date__then_booking_found() {
      given_existingBooking(2, 3);

      when_findBookingsForDateRange(1, 3);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|-|---S|-|E-")
    void
        given_booking_start_date_before_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
      given_existingBooking(2, 4);

      when_findBookingsForDateRange(1, 3);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|-|----|S|E-")
    void
        given_booking_start_date_equals_to_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
      given_existingBooking(3, 4);

      when_findBookingsForDateRange(1, 3);

      then_assertBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("--|-|----|-|SE")
    void given_booking_dates_after_range_end_date__then_no_booking_found() {
      given_existingBooking(3, 4);

      when_findBookingsForDateRange(1, 2);

      then_assertNoBookingFoundForDateRange();
    }

    @Test
    @DisplayNamePrefix("-S|-|----|-|E-")
    void given_booking_dates_overlap_range_dates__then_booking_found() {
      given_existingBooking(1, 4);

      when_findBookingsForDateRange(2, 3);

      then_assertBookingFoundForDateRange();
    }

    private void when_findBookingsForDateRange(int startPlusDays, int endPlusDays) {
      bookingsForDateRange =
          classUnderTest.findForDateRange(
              now.plusDays(startPlusDays), now.plusDays(endPlusDays), CAMPSITE_ID);
    }

    private void then_assertNoBookingFoundForDateRange() {
      assertThat(bookingsForDateRange).isEmpty();
    }

    private void then_assertBookingFoundForDateRange() {
      assertAll(
          () -> assertThat(bookingsForDateRange).size().isEqualTo(1),
          () -> assertThat(existingBookingEntity).isIn(bookingsForDateRange));
    }
  }
}
