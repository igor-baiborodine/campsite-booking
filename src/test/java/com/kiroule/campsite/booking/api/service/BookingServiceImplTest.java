package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.*;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.LENIENT;

import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.exception.IllegalBookingStateException;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class BookingServiceImplTest {

  @Mock BookingRepository bookingRepository;

  @Spy BookingMapper bookingMapper;

  @InjectMocks BookingServiceImpl classUnderTest;

  LocalDate now;
  UUID uuid;
  Booking existingBooking;
  BookingEntity existingBookingEntity;
  Booking newBooking;

  BookingEntity newBookingEntity;
  Booking createdBooking;

  @BeforeEach
  void beforeEach() {
    now = LocalDate.now();
    uuid = UUID.randomUUID();
    existingBooking = null;
    existingBookingEntity = null;
    newBooking = null;
    newBookingEntity = null;
    createdBooking = null;
  }

  private void given_newBooking(int startPlusDays, int endPlusDays) {
    newBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
    newBooking.setActive(false);
    assumeTrue(newBooking.isNew());
    assumeFalse(newBooking.isActive());
  }

  private void given_existingBookingEntity(int startPlusDays, int endPlusDays) {
    existingBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    existingBooking.setId(1L);
    existingBooking.setVersion(0L);

    existingBookingEntity =
        buildBookingEntity(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    existingBookingEntity.setId(1L);
    existingBookingEntity.setVersion(0L);
  }

  private void given_existingBookingEntityFoundForUuidInRepository() {
    doReturn(Optional.of(existingBookingEntity)).when(bookingRepository).findByUuid(any());
    doReturn(existingBooking).when(bookingMapper).toBooking(any(BookingEntity.class));
    doReturn(existingBookingEntity).when(bookingMapper).toBookingEntity(any(Booking.class));
  }

  @Nested
  class FindBookingByUuid {

    Booking foundBooking;

    @BeforeEach
    void beforeEach() {
      foundBooking = null;
    }

    @Test
    void happy_path() {
      given_existingBookingEntity(1, 2);
      given_existingBookingEntityFoundForUuidInRepository();

      when_findBookingByUuid();

      then_assertBookingFound();
    }

    @Test
    void given_non_existing_booking_uuid__then_booking_not_found_and_exception_thrown() {
      given_noExistingBookingFoundForUuidInRepository();

      when_findBookingByUuidThenAssertExceptionThrown(BookingNotFoundException.class);
    }

    private void given_noExistingBookingFoundForUuidInRepository() {
      doReturn(Optional.empty()).when(bookingRepository).findByUuid(any());
    }

    private void when_findBookingByUuid() {
      foundBooking = classUnderTest.findByUuid(uuid);
    }

    private void then_assertBookingFound() {
      assertThat(foundBooking).isEqualTo(existingBooking);
      verify(bookingRepository).findByUuid(uuid);
      verify(bookingMapper).toBooking(existingBookingEntity);
    }

    private void when_findBookingByUuidThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.findByUuid(any()));
    }
  }

  @Nested
  @Disabled
  class CreateBooking {

    @Test
    void happy_path() {
      given_newBooking(1, 4);
      given_foundNoExistingBookingEntitiesForDateRangeInRepository();

      when_createBooking();

      then_assertBookingCreated();
    }

    @Test
    void
        given_booking_dates_not_available__then_booking_dates_not_available_and_exception_thrown() {
      given_existingBookingEntity(1, 4);
      given_newBooking(1, 4);
      given_foundExistingBookingForDateRangeInRepository();

      when_createBookingFromNewBookingThenAssertExceptionThrown(
          BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_exists__then_illegal_booking_state_exception_thrown() {
      given_existingBookingEntity(1, 2);

      when_createBookingFromExistingBookingThenAssertExceptionThrown(
          IllegalBookingStateException.class);
    }

    private void given_foundExistingBookingForDateRangeInRepository() {
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_foundNoExistingBookingEntitiesForDateRangeInRepository() {
      doReturn(emptyList())
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
      doReturn(newBookingEntity).when(bookingMapper).toBookingEntity(any());
    }

    private void when_createBooking() {
      createdBooking = classUnderTest.createBooking(newBooking);
    }

    private void then_assertBookingCreated() {
      assertThat(newBooking.isActive()).isTrue();
      verify(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(
              newBooking.getStartDate(), newBooking.getEndDate(), newBooking.getCampsiteId());
      verify(bookingRepository).save(newBookingEntity);
      verify(bookingMapper).toBookingEntity(newBooking);
    }

    private void when_createBookingFromNewBookingThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.createBooking(newBooking));
    }

    private void when_createBookingFromExistingBookingThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.createBooking(existingBooking));
    }
  }

  @Nested
  @Disabled
  class FindVacantDates {

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
    @DisplayNamePrefix("--|-|----|-|--")
    void happy_path() {
      given_dateRange(1, 4);
      given_noExistingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(
          startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList()));
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
      given_existingBookingEntity(1, 4);
      given_existingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(EMPTY_LIST);
    }

    @Test
    @DisplayNamePrefix("--|S|----|E|--")
    void given_booking_dates_same_as_range_dates__then_end_date_found() {
      given_dateRange(1, 4);
      given_existingBookingEntity(1, 4);
      given_existingBookingFoundForDateRangeInRepository();

      when_findVacantDays();

      then_assertVacantDaysFound(singletonList(endDate));
    }

    private void given_noExistingBookingFoundForDateRangeInRepository() {
      doReturn(EMPTY_LIST).when(bookingRepository).findForDateRange(any(), any(), any());
    }

    private void given_existingBookingFoundForDateRangeInRepository() {
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRange(any(), any(), any());
    }

    private void given_dateRange(int startPlusDays, int endPlusDays) {
      startDate = now.plusDays(startPlusDays);
      endDate = now.plusDays(endPlusDays);
    }

    private void when_findVacantDays() {
      vacantDates = classUnderTest.findVacantDays(startDate, endDate, CAMPSITE_ID);
    }

    private void when_findVacantDaysThenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.findVacantDays(startDate, endDate, CAMPSITE_ID));
    }

    private void then_assertVacantDaysFound(List<LocalDate> expected) {
      assertThat(vacantDates).hasSize(expected.size()).hasSameElementsAs(expected);
    }
  }

  @Nested
  class UpdateBooking {

    Booking otherExistingBooking;
    BookingEntity otherExistingBookingEntity;
    Booking existingBookingWithNewBookingDates;
    BookingEntity existingBookingEntityWithNewBookingDates;

    @BeforeEach
    void beforeEach() {
      otherExistingBooking = null;
      otherExistingBookingEntity = null;
      existingBookingWithNewBookingDates = null;
      existingBookingEntityWithNewBookingDates = null;
    }

    @Test
    void happy_path() {
      given_existingBookingEntity(1, 2);
      given_existingBookingEntityFoundForUuidInRepository();
      given_existingBookingFoundForDateRangeInRepository();
      given_existingBookingEntityWithNewBookingDates(1, 3);

      when_updateBooking();

      then_assertBookingUpdated();
    }

    @Test
    @Disabled
    void given_existing_booking_canceled__then_illegal_booking_state_exception_thrown() {
      given_existingBookingEntity(1, 2);
      given_existingBookingEntityCanceled();
      given_existingBookingEntityFoundForUuidInRepository();

      when_updateBookingThenAssertExceptionThrown(IllegalBookingStateException.class);
    }

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_exception_thrown() {
      given_existingBookingEntity(1, 2);
      given_existingBookingEntityFoundForUuidInRepository();
      given_otherExistingBookingEntity(2, 3);
      given_twoBookingsFoundForDateRangeInRepository();

      when_updateBookingWithNewBookingDatesThenAssertExceptionThrown(
          1, 3, BookingDatesNotAvailableException.class);
    }

    private void given_existingBookingEntityCanceled() {
      existingBookingEntity.setActive(false);
    }

    private void given_otherExistingBookingEntity(int startPlusDays, int endPlusDays) {
      otherExistingBooking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays));
      otherExistingBooking.setId(1L);
      otherExistingBooking.setVersion(0L);

      otherExistingBookingEntity =
          buildBookingEntity(
              now.plusDays(startPlusDays),
              now.plusDays(endPlusDays),
              otherExistingBooking.getUuid());
      otherExistingBookingEntity.setId(1L);
      otherExistingBookingEntity.setVersion(0L);
    }

    private void given_twoBookingsFoundForDateRangeInRepository() {
      doReturn(asList(existingBookingEntity, otherExistingBookingEntity))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_existingBookingFoundForDateRangeInRepository() {
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
    }

    private void given_existingBookingEntityWithNewBookingDates(
        int startPlusDays, int endPlusDays) {
      existingBookingWithNewBookingDates =
          buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
      existingBookingEntityWithNewBookingDates =
          bookingMapper.toBookingEntity(existingBookingWithNewBookingDates);
    }

    private void when_updateBookingThenAssertExceptionThrown(Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.updateBooking(existingBooking));
    }

    private void when_updateBookingWithNewBookingDatesThenAssertExceptionThrown(
        int startPlusDays, int endPlusDays, Class<? extends Exception> exception) {
      Booking existingBookingWithUpdatedBookingDates =
          buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
      assertThrows(
          exception, () -> classUnderTest.updateBooking(existingBookingWithUpdatedBookingDates));
    }

    private void when_updateBooking() {
      classUnderTest.updateBooking(existingBookingWithNewBookingDates);
    }

    private void then_assertBookingUpdated() {
      assertThat(existingBookingWithNewBookingDates.isActive()).isTrue();
      verify(bookingRepository).saveAndFlush(existingBookingEntityWithNewBookingDates);
      verify(bookingMapper, times(2)).toBookingEntity(existingBookingWithNewBookingDates);
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
    void happy_path() {
      given_existingBookingEntity(1, 2);
      given_existingBookingEntityFoundForUuidInRepository();
      given_cancelledBookingSavedInRepository();

      then_cancelBooking();

      then_assertCancelledBooking();
    }

    private void given_cancelledBookingSavedInRepository() {
      doReturn(existingBookingEntity).when(bookingRepository).save(any());
    }

    private void then_cancelBooking() {
      cancelled = classUnderTest.cancelBooking(existingBookingEntity.getUuid());
    }

    private void then_assertCancelledBooking() {
      assertThat(cancelled).isTrue();
      verify(bookingRepository).findByUuid(existingBookingEntity.getUuid());
      verify(bookingRepository).save(existingBookingEntity);
      verify(bookingMapper).toBookingEntity(existingBooking);
      verify(bookingMapper, times(2)).toBooking(existingBookingEntity);
    }
  }
}
