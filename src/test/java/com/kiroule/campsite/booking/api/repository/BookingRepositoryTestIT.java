package com.kiroule.campsite.booking.api.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.TestHelper;
import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.mapper.BookingMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class BookingRepositoryTestIT {

  @Autowired
  BookingRepository bookingRepository;

  UUID uuid;
  Booking existingBooking;
  Optional<Booking> bookingOptionalForUuid;
  List<Booking> bookingsForDateRange;

  @BeforeEach
  void beforeEach() {
    bookingRepository.deleteAll();
    uuid = UUID.randomUUID();
    existingBooking = null;
    bookingOptionalForUuid = null;
    bookingsForDateRange = null;
  }

  @Test
  void find_by_uuid__given_existing_booking__then_booking_found() {
    givenExistingBooking(1, 2);

    whenFindByUuid();

    thenAssertBookingFoundForUuid();
  }

  @Test
  @DisplayNamePrefix("-S-E|-|----|-|--")
  void find_for_date_range__given_booking_dates_before_range_startDate__then_no_booking_found() {
    givenExistingBooking(1, 2);

    whenFindForDateRange(3, 4);

    thenAssertNoBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("-S|E|----|-|--")
  void find_for_date_range__given_booking_start_date_before_range_start_date_and_booking_end_date_equals_to_range_start_date__then_no_booking_found() {
    givenExistingBooking(1, 2);

    whenFindForDateRange(2, 3);

    thenAssertNoBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("-S|-|E---|-|--")
  void find_for_date_range__given_booking_start_date_before_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
    givenExistingBooking(1, 3);

    whenFindForDateRange(2, 4);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|S|E---|-|--")
  void find_for_date_range__given_booking_start_date_equals_to_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
    givenExistingBooking(1, 2);

    whenFindForDateRange(1, 3);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|-|S--E|-|--")
  void find_for_date_range__given_booking_dates_within_range_dates__then_booking_found() {
    givenExistingBooking(2, 3);

    whenFindForDateRange(1, 4);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|-|---S|E|--")
  void find_for_date_range__given_booking_start_date_within_range_dates_and_booking_end_date_equals_to_range_end_date__then_booking_found() {
    givenExistingBooking(2, 3);

    whenFindForDateRange(1, 3);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|-|---S|-|E-")
  void find_for_date_range__given_booking_start_date_before_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
    givenExistingBooking(2, 4);

    whenFindForDateRange(1, 3);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|-|----|S|E-")
  void find_for_date_range__given_booking_start_date_equals_to_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
    givenExistingBooking(3, 4);

    whenFindForDateRange(1, 3);

    thenAssertBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("--|-|----|-|S-E-")
  void find_for_date_range__given_booking_dates_after_range_end_date__then_no_booking_found() {
    givenExistingBooking(3, 4);

    whenFindForDateRange(1, 2);

    thenAssertNoBookingFoundForDateRange();
  }

  @Test
  @DisplayNamePrefix("-S|-|----|-|E-")
  void find_for_date_range__given_booking_dates_overlap_range_dates__then_booking_found() {
    givenExistingBooking(1, 4);

    whenFindForDateRange(2, 3);

    thenAssertBookingFoundForDateRange();
  }

  private void givenExistingBooking(int startPlusDays, int endPlusDays) {
    BookingDto bookingDto = TestHelper.buildBookingDto(
        LocalDate.now().plusDays(endPlusDays), LocalDate.now().plusDays(startPlusDays), uuid);
    existingBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(bookingDto));
  }

  private void whenFindByUuid() {
    bookingOptionalForUuid = bookingRepository.findByUuid(uuid);
  }

  private void thenAssertBookingFoundForUuid() {
    assertAll("foundBooking",
        () -> assertThat(bookingOptionalForUuid).hasValue(existingBooking),
        () -> assertThat(bookingOptionalForUuid.get().getCreatedAt().toLocalDate())
            .isEqualTo(LocalDate.now()));
  }

  private void whenFindForDateRange(int startPlusDays, int endPlusDays) {
    bookingsForDateRange = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(startPlusDays), LocalDate.now().plusDays(endPlusDays));
  }

  private void thenAssertNoBookingFoundForDateRange() {
    assertThat(bookingsForDateRange).isEmpty();
  }

  private void thenAssertBookingFoundForDateRange() {
    assertAll(
        () -> assertThat(bookingsForDateRange).size().isEqualTo(1),
        () -> assertThat(existingBooking).isIn(bookingsForDateRange)
    );
  }
}
