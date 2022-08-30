package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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

    Booking foundBooking;

    @BeforeEach
    void beforeEach() {
      foundBooking = null;
    }

    @Test
    void given_non_existing_booking_uuid__then_booking_not_found_and_exception_thrown() {
      given_noExistingBookingFoundForUuidInRepository();

      when_findBookingByUuidThenAssertExceptionThrown(BookingNotFoundException.class);
    }

    @Test
    void given_existing_booking_uuid__then_booking_found() {
      given_existingBooking(1, 2);
      given_existingBookingFoundForUuidInRepository();

      when_findBookingByUuid();

      then_assertBookingFound();
    }

    private void given_noExistingBookingFoundForUuidInRepository() {
      doReturn(Optional.empty()).when(bookingRepository).findByUuid(any());
    }

    private void when_findBookingByUuid() {
      foundBooking = bookingService.findByUuid(uuid);
    }

    private void then_assertBookingFound() {
      assertThat(foundBooking).isEqualTo(existingBooking);
      verify(bookingRepository).findByUuid(uuid);
    }

    private void when_findBookingByUuidThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.findByUuid(any()));
    }
  }

  @Nested
  class Create_Booking {

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_and_exception_thrown() {
      given_existingBooking(1, 4);
      given_newBooking(1, 4);
      given_foundExistingBookingForDateRangeInRepository();

      when_createBookingFromNewBookingThenAssertExceptionThrown(
          BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_dates_available__booking_created() {
      given_newBooking(1, 4);
      given_foundNoExistingBookingsForDateRangeInRepository();

      when_createBooking();

      then_assertBookingCreated();
    }

    @Test
    void given_booking_is_not_new__then_illegal_booking_state_exception_thrown() {
      given_existingBooking(1, 2);

      when_createBookingFromExistingBookingThenAssertExceptionThrown(
          IllegalBookingStateException.class);
    }

    private void given_foundExistingBookingForDateRangeInRepository() {
      doReturn(singletonList(existingBooking))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_foundNoExistingBookingsForDateRangeInRepository() {
      doReturn(EMPTY_LIST).when(bookingRepository).findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void when_createBooking() {
      bookingService.createBooking(newBooking);
    }

    private void then_assertBookingCreated() {
      assertThat(newBooking.isActive()).isTrue();
      verify(bookingRepository).findForDateRangeWithPessimisticWriteLocking(
          newBooking.getStartDate(), newBooking.getEndDate(), newBooking.getCampsiteId());
      verify(bookingRepository).save(newBooking);
    }

    private void when_createBookingFromNewBookingThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.createBooking(newBooking));
    }

    private void when_createBookingFromExistingBookingThenAssertExceptionThrown(
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
      given_dateRange(0, 2);

      when_findVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    void given_range_end_date_is_now__then_illegal_argument_exception_thrown() {
      given_dateRange(2, 0);

      when_findVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    void given_range_end_date_is_before_range_start_date__then_illegal_argument_exception_thrown() {
      given_dateRange(3, 1);

      when_findVacantDaysThenAssertExceptionThrown(IllegalArgumentException.class);
    }

    @Test
    @DisplayNamePrefix("-S|-|----|-|E-")
    void given_booking_dates_overlap_range_dates__then_no_vacant_dates_found() {
      given_dateRange(2, 3);
      given_existingBooking(1, 4);
      given_existingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(EMPTY_LIST);
    }

    @Test
    @DisplayNamePrefix("--|S|----|E|--")
    void given_booking_dates_same_as_range_dates__then_end_date_found() {
      given_dateRange(1, 4);
      given_existingBooking(1, 4);
      given_existingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(singletonList(endDate));
    }

    @Test
    @DisplayNamePrefix("--|-|----|-|--")
    void given_no_existing_bookings__then_vacant_dates_within_date_range_inclusive_found() {
      given_dateRange(1, 4);
      given_noExistingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(
          startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()));
    }

    private void given_noExistingBookingFoundForDateRangeInRepository() {
      doReturn(EMPTY_LIST).when(bookingRepository).findForDateRange(any(), any(), any());
    }

    private void given_existingBookingFoundForDateRangeInRepository() {
      doReturn(singletonList(existingBooking))
          .when(bookingRepository)
          .findForDateRange(any(), any(), any());
    }

    private void given_dateRange(int startPlusDays, int endPlusDays) {
      startDate = now.plusDays(startPlusDays);
      endDate = now.plusDays(endPlusDays);
    }

    private void when_findVacantDays() {
      vacantDates = bookingService.findVacantDays(startDate, endDate, CAMPSITE_ID);
    }

    private void when_findVacantDaysThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.findVacantDays(startDate, endDate, CAMPSITE_ID));
    }

    private void then_assertVacantDaysFound(List<LocalDate> expected) {
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
      given_existingBooking(1, 2);
      given_existingBookingCanceled();
      given_existingBookingFoundForUuidInRepository();

      when_updateBookingThenAssertExceptionThrown(IllegalBookingStateException.class);
    }

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_exception_thrown() {
      given_existingBooking(1, 2);
      given_existingBookingFoundForUuidInRepository();
      given_otherExistingBooking(2, 3);
      given_twoBookingsFoundForDateRangeInRepository();

      when_updateBookingWithNewBookingDatesThenAssertExceptionThrown(
          1, 3, BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_dates_available__then_booking_updated() {
      given_existingBooking(1, 2);
      given_existingBookingFoundForUuidInRepository();
      given_existingBookingFoundForDateRangeInRepository();
      given_existingBookingWithNewBookingDates(1, 3);

      when_updateBooking();

      then_assertBookingUpdated();
    }

    private void given_existingBookingCanceled() {
      existingBooking.setActive(false);
    }

    private void given_otherExistingBooking(int startPlusDays, int endPlusDays) {
      otherExistingBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
      otherExistingBooking.setId(1L);
      otherExistingBooking.setActive(true);

      assumeFalse(otherExistingBooking.isNew());
      assumeTrue(otherExistingBooking.isActive());
    }

    private void given_twoBookingsFoundForDateRangeInRepository() {
      doReturn(asList(existingBooking, otherExistingBooking))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_existingBookingFoundForDateRangeInRepository() {
      doReturn(singletonList(existingBooking))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_existingBookingWithNewBookingDates(int startPlusDays, int endPlusDays) {
      existingBookingWithNewBookingDates =
          buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    }

    private void when_updateBookingThenAssertExceptionThrown(Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.updateBooking(existingBooking));
    }

    private void when_updateBookingWithNewBookingDatesThenAssertExceptionThrown(
        int startPlusDays, int endPlusDays, Class<? extends Exception> exception) {
      Booking existingBookingWithUpdatedBookingDates =
          buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
      assertThrows(
          exception, () -> bookingService.updateBooking(existingBookingWithUpdatedBookingDates));
    }

    private void when_updateBooking() {
      bookingService.updateBooking(existingBookingWithNewBookingDates);
    }

    private void then_assertBookingUpdated() {
      assertThat(existingBookingWithNewBookingDates.isActive()).isTrue();
      verify(bookingRepository).save(existingBookingWithNewBookingDates);
    }
  }

  @Nested
  class CancelBooking {

    Boolean cancelled;

    @BeforeEach
    void beforeEach() {
      cancelled = null;
    }

    @Test
    void given_existing_booking__then_booking_cancelled() {
      given_existingBooking(1, 2);
      given_existingBookingFoundForUuidInRepository();
      given_cancelledBookingSavedInRepository();

      then_cancelBooking();

      then_assertCancelledBooking();
    }

    private void given_cancelledBookingSavedInRepository() {
      doReturn(existingBooking).when(bookingRepository).save(any());
    }

    private void then_cancelBooking() {
      cancelled = bookingService.cancelBooking(existingBooking.getUuid());
    }

    private void then_assertCancelledBooking() {
      assertThat(cancelled).isTrue();
      verify(bookingRepository).findByUuid(existingBooking.getUuid());
      verify(bookingRepository).save(existingBooking);
    }

  }

  private void given_newBooking(int startPlusDays, int endPlusDays) {
    newBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    newBooking.setActive(false);
    assumeTrue(newBooking.isNew());
    assumeFalse(newBooking.isActive());
  }

  private void given_existingBooking(int startPlusDays, int endPlusDays) {
    existingBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    existingBooking.setId(1L);
    existingBooking.setVersion(0L);

    assumeFalse(existingBooking.isNew());
    assumeTrue(existingBooking.isActive());
  }

  private void given_existingBookingFoundForUuidInRepository() {
    doReturn(Optional.of(existingBooking)).when(bookingRepository).findByUuid(any());
  }

}
