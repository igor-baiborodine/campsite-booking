package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Lists;
import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.exception.IllegalBookingStateException;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class BookingServiceImplTest {

  @Mock
  BookingRepository bookingRepository;

  @InjectMocks
  BookingServiceImpl bookingService;

  LocalDate now;
  UUID uuid;
  Booking existingBooking;
  Booking newBooking;

  @BeforeEach
  void beforeEach() {
    now = LocalDate.now();
    uuid = UUID.randomUUID();
    existingBooking = null;
    newBooking = null;
  }

  @Nested
  class Find_Booking_By_Uuid {

    Booking bookingForUuid;

    @BeforeEach
    void beforeEach() {
      bookingForUuid = null;
    }

    @Test
    void given_non_existing_booking_uuid__then_booking_not_found_exception_thrown() {
      givenFoundNoExistingBookingForUuid();

      whenFindBookingByUuidThenAssertExceptionThrown(BookingNotFoundException.class);
    }

    @Test
    void given_existing_booking_uuid__then_booking_found() {
      givenExistingBooking(1, 2);
      givenFoundExistingBookingForUuid();
      
      whenFindBookingByUuid();

      thenAssertBookingFound();
    }

    private void givenFoundNoExistingBookingForUuid() {
      doReturn(Optional.empty()).when(bookingRepository).findByUuid(uuid);
    }

    private void givenFoundExistingBookingForUuid() {
      doReturn(Optional.of(existingBooking)).when(bookingRepository).findByUuid(uuid);
    }

    private void whenFindBookingByUuid() {
      bookingForUuid = bookingService.findBookingByUuid(uuid);
    }

    private void thenAssertBookingFound() {
      assertThat(bookingForUuid).isEqualTo(existingBooking);
    }

    private void whenFindBookingByUuidThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.findBookingByUuid(uuid));
    }
  }

  @Nested
  class Create_Booking {

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_exception_thrown() {
      givenExistingBooking(1, 4);
      givenNewBooking(1, 4);
      givenFoundExistingBookingForDateRange(1, 4);

      whenCreateBookingFromNewBookingThenAssertExceptionThrown(
          BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_dates_available__booking_created() {
      givenNewBooking(1, 4);
      givenFoundNoExistingBookingsForDateRange(1, 4);

      whenCreateBookingFromNewBooking();

      thenAssertBookingCreated();
    }

    @Test
    void given_booking_is_not_new__then_illegal_booking_state_exception_thrown() {
      givenExistingBooking(1, 2);

      whenCreateBookingFromExistingBookingThenAssertExceptionThrown(
          IllegalBookingStateException.class);
    }

    private void givenFoundExistingBookingForDateRange(int startPlusDays, int endPlusDays) {
      doReturn(Lists.newArrayList(existingBooking)).when(bookingRepository)
          .findForDateRange(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    }

    private void givenFoundNoExistingBookingsForDateRange(int startPlusDays, int endPlusDays) {
      doReturn(Lists.newArrayList()).when(bookingRepository)
          .findForDateRange(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    }

    private void whenCreateBookingFromNewBooking() {
      bookingService.createBooking(newBooking);
    }

    private void thenAssertBookingCreated() {
      assertThat(newBooking.isActive()).isTrue();
      verify(bookingRepository, times(1)).save(newBooking);
    }

    private void whenCreateBookingFromNewBookingThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.createBooking(newBooking));
    }

    private void whenCreateBookingFromExistingBookingThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.createBooking(existingBooking));
    }
  }

  @Nested
  class Find_Vacant_Dates {

    LocalDate startDate;
    LocalDate endDate;
    List<LocalDate> vacantDates;

    @BeforeEach
    void beforeEach() {
      startDate = null;
      endDate = null;
      vacantDates = null;
    }

    @Test
    void given_range_start_date_is_now__then_illegal_argument_exception_thrown() {
      givenDateRange(0, 2);

      whenFindVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    void given_range_end_date_is_now__then_illegal_argument_exception_thrown() {
      givenDateRange(2, 0);

      whenFindVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    void given_range_end_date_is_before_range_start_date__then_illegal_argument_exception_thrown() {
      givenDateRange(3, 1);

      whenFindVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    @DisplayNamePrefix("-S|-|----|-|E-")
    void given_booking_dates_overlap_range_dates__then_no_vacant_dates_found() {
      givenDateRange(2, 3);
      givenExistingBooking(1, 4);
      givenExistingBookingFoundForDateRange();

      whenFindVacantDays();

      thenAssertVacantDaysFound(Lists.newArrayList());
    }

    @Test
    @DisplayNamePrefix("--|S|----|E|--")
    void given_booking_dates_same_as_range_dates__then_end_date_found() {
      givenDateRange(1, 4);
      givenExistingBooking(1, 4);
      givenExistingBookingFoundForDateRange();

      whenFindVacantDays();

      thenAssertVacantDaysFound(Lists.newArrayList(endDate));
    }


    @Test
    @DisplayNamePrefix("--|-|----|-|--")
    void given_no_existing_bookings__then_vacant_dates_within_date_range_inclusive_found() {
      givenDateRange(1, 4);
      givenNoExistingBookingFoundForDateRange();

      whenFindVacantDays();

      thenAssertVacantDaysFound(
          startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()));
    }

    private void givenNoExistingBookingFoundForDateRange() {
      doReturn(Lists.newArrayList())
          .when(bookingRepository).findForDateRange(startDate, endDate);
    }

    private void givenExistingBookingFoundForDateRange() {
      doReturn(Lists.newArrayList(existingBooking))
          .when(bookingRepository).findForDateRange(startDate, endDate);
    }

    private void givenDateRange(int startPlusDays, int endPlusDays) {
      startDate = now.plusDays(startPlusDays);
      endDate = now.plusDays(endPlusDays);
    }

    private void whenFindVacantDays() {
      vacantDates = bookingService.findVacantDays(startDate, endDate);
    }

    private void whenFindVacantDaysThenAssertExceptionThrown(Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.findVacantDays(startDate, endDate));
    }

    private void thenAssertVacantDaysFound(List<LocalDate> expected) {
      assertThat(vacantDates).hasSize(expected.size()).hasSameElementsAs(expected);
    }
  }

  @Nested
  class Update_Booking {

    Booking otherExistingBooking;
    Booking existingBookingWithNewBookingDates;

    @BeforeEach
    void beforeEach() {
      otherExistingBooking = null;
      existingBookingWithNewBookingDates = null;
    }

    @Test
    void given_existing_booking_canceled__then_illegal_booking_state_exception_thrown() {
      givenExistingBooking(1, 2);
      givenExistingBookingCanceled();
      givenExistingBookingFoundForUuid();

      whenUpdateBookingThenAssertExceptionThrown(IllegalBookingStateException.class);
    }

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_exception_thrown() {
      givenExistingBooking(1, 2);
      givenExistingBookingFoundForUuid();
      givenOtherExistingBooking(2, 3);
      givenTwoBookingsFoundForDateRange(1, 3);

      whenUpdateBookingWithNewBookingDatesThenAssertExceptionThrown(
          1, 3, BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_dates_available__then_booking_updated() {
      givenExistingBooking(1, 2);
      givenExistingBookingFoundForUuid();
      givenExistingBookingFoundForDateRange(1, 3);
      givenExistingBookingWithNewBookingDates(1, 3);

      whenUpdateBooking();

      thenAssertBookingUpdated();
    }

    private void givenExistingBookingCanceled() {
      existingBooking.setActive(false);
    }

    private void givenExistingBookingFoundForUuid() {
      doReturn(Optional.of(existingBooking)).when(bookingRepository).findByUuid(uuid);
    }

    private void givenOtherExistingBooking(int startPlusDays, int endPlusDays) {
      otherExistingBooking = buildBooking(
          now.plusDays(startPlusDays), now.plusDays(endPlusDays));
      otherExistingBooking.setId(1L);
      otherExistingBooking.setActive(true);

      assumeFalse(otherExistingBooking.isNew());
      assumeTrue(otherExistingBooking.isActive());
    }

    private void givenTwoBookingsFoundForDateRange(int startPlusDays, int endPlusDays) {
      doReturn(Lists.newArrayList(existingBooking, otherExistingBooking))
          .when(bookingRepository).findForDateRange(
              now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    }

    private void givenExistingBookingFoundForDateRange(int startPlusDays, int endPlusDays) {
      doReturn(Lists.newArrayList(existingBooking)).when(bookingRepository)
          .findForDateRange(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    }

    private void givenExistingBookingWithNewBookingDates(int startPlusDays, int endPlusDays) {
      existingBookingWithNewBookingDates = buildBooking(
          now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    }

    private void whenUpdateBookingThenAssertExceptionThrown(Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.updateBooking(existingBooking));
    }

    private void whenUpdateBookingWithNewBookingDatesThenAssertExceptionThrown(
        int startPlusDays, int endPlusDays, Class<? extends Exception> exception) {
      Booking existingBookingWithUpdatedBookingDates = buildBooking(
          now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
      assertThrows(
          exception, () -> bookingService.updateBooking(existingBookingWithUpdatedBookingDates));
    }

    private void whenUpdateBooking() {
      bookingService.updateBooking(existingBookingWithNewBookingDates);
    }

    private void thenAssertBookingUpdated() {
      assertThat(existingBookingWithNewBookingDates.isActive()).isTrue();
      verify(bookingRepository, times(1)).save(existingBookingWithNewBookingDates);
    }
  }

  private void givenNewBooking(int startPlusDays, int endPlusDays) {
    newBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    newBooking.setActive(false);
    assumeTrue(newBooking.isNew());
    assumeFalse(newBooking.isActive());
  }

  private void givenExistingBooking(int startPlusDays, int endPlusDays) {
    existingBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    existingBooking.setId(1L);
    existingBooking.setVersion(0L);
    
    assumeFalse(existingBooking.isNew());
    assumeTrue(existingBooking.isActive());
  }
}