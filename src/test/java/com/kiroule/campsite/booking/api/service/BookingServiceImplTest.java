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
import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.exception.IllegalBookingStateException;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
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
public class BookingServiceImplTest {

  @Mock
  BookingRepository bookingRepository;

  @InjectMocks
  BookingServiceImpl bookingService;

  UUID uuid;
  Booking existingBooking;
  Booking newBooking;

  @BeforeEach
  void beforeEach() {
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
    void given_existing_booking_uuid__then_booking_found() {
      givenExistingBooking(1, 2);

      whenFindBookingByUuid();

      thenAssertBookingFound();
    }

    @Test
    void given_non_existing_booking_uuid__then_booking_not_found_exception_thrown() {
      givenNonExistingBooking();

      whenFindBookingByUuidThenAssertExceptionThrown();
    }

    private void givenExistingBooking(int startPlusDays, int endPlusDays) {
      BookingServiceImplTest.this.givenExistingBooking(startPlusDays, endPlusDays);
      existingBooking.setActive(true);
      doReturn(Optional.of(existingBooking)).when(bookingRepository).findByUuid(uuid);
    }

    private void givenNonExistingBooking() {
      doReturn(Optional.empty()).when(bookingRepository).findByUuid(uuid);
    }

    private void whenFindBookingByUuid() {
      bookingForUuid = bookingService.findBookingByUuid(uuid);
    }

    private void thenAssertBookingFound() {
      assertThat(bookingForUuid).isEqualTo(existingBooking);
    }

    private void whenFindBookingByUuidThenAssertExceptionThrown() {
      assertThrows(BookingNotFoundException.class, () -> bookingService.findBookingByUuid(uuid));
    }
  }

  @Nested
  class Create_Booking {

    @Test
    void given_booking_dates_available__booking_created() {
      givenBookingDatesAvailable(1, 4);

      whenCreateBooking();

      thenAssertBookingCreated();
    }

    @Test
    void given_booking_dates_not_available__then_booking_dates_not_available_exception_thrown() {
      givenBookingDatesNotAvailable(1, 4);

      whenCreateBookingThenAssertExceptionThrown(newBooking, BookingDatesNotAvailableException.class);
    }

    @Test
    void given_booking_is_not_new__then_illegal_booking_state_exception_thrown() {
      givenExistingBooking(1, 2);

      whenCreateBookingThenAssertExceptionThrown(existingBooking, IllegalBookingStateException.class);
    }

    private void givenBookingDatesAvailable(int startPlusDays, int endPlusDays) {
      BookingServiceImplTest.this.givenNewBooking(startPlusDays, endPlusDays);
      doReturn(Lists.newArrayList()).when(bookingRepository)
          .findForDateRange(newBooking.getStartDate(), newBooking.getEndDate());
    }

    private void givenBookingDatesNotAvailable(int startPlusDays, int endPlusDays) {
      newBooking = buildBooking(
          LocalDate.now().plusDays(startPlusDays), LocalDate.now().plusDays(endPlusDays));
      assumeTrue(newBooking.isNew());
      assumeFalse(newBooking.isActive());

      BookingServiceImplTest.this.givenExistingBooking(startPlusDays, endPlusDays);
      doReturn(Lists.newArrayList(existingBooking)).when(bookingRepository)
          .findForDateRange(newBooking.getStartDate(), newBooking.getEndDate());
    }

    private void whenCreateBooking() {
      bookingService.createBooking(newBooking);
    }

    private void thenAssertBookingCreated() {
      assertThat(newBooking.isActive()).isTrue();
      verify(bookingRepository, times(1)).save(newBooking);
    }

    private void whenCreateBookingThenAssertExceptionThrown(
        Booking booking, Class<? extends Exception> exception) {
      assertThrows(exception, () -> bookingService.createBooking(booking));
    }
  }

  private void givenNewBooking(int startPlusDays, int endPlusDays) {
    newBooking = buildBooking(
        LocalDate.now().plusDays(startPlusDays), LocalDate.now().plusDays(endPlusDays));
    assumeTrue(newBooking.isNew());
    assumeFalse(newBooking.isActive());
  }

  private void givenExistingBooking(int startPlusDays, int endPlusDays) {
    existingBooking = buildBooking(
        LocalDate.now().plusDays(endPlusDays), LocalDate.now().plusDays(startPlusDays), uuid);
    existingBooking.setId(1L);
    existingBooking.setActive(true);

    assumeFalse(existingBooking.isNew());
    assumeTrue(existingBooking.isActive());
  }

//
//  @Test
//  public void findVacantDate_rangeStartDateIsNow_illegalArgumentExceptionThrown() {
//    exception.expect(IllegalArgumentException.class);
//    // given
//    LocalDate startDate = LocalDate.now();
//    LocalDate endDate = LocalDate.now().plusDays(2);
//    // when
//    bookingService.findVacantDays(startDate, endDate);
//    // then
//    // IllegalArgumentException is thrown
//  }
//
//  @Test
//  public void findVacantDate_rangeEndDateIsNow_illegalArgumentExceptionThrown() {
//    exception.expect(IllegalArgumentException.class);
//    // given
//    LocalDate startDate = LocalDate.now().plusDays(2);
//    LocalDate endDate = LocalDate.now();
//    // when
//    bookingService.findVacantDays(startDate, endDate);
//    // then
//    // IllegalArgumentException is thrown
//  }
//
//  @Test
//  public void findVacantDate_rangeEndDateIsBeforeRangeStartDate_illegalArgumentExceptionThrown() {
//    exception.expect(IllegalArgumentException.class);
//    // given
//    LocalDate startDate = LocalDate.now().plusDays(2);
//    LocalDate endDate = LocalDate.now().plusDays(1);
//    // when
//    bookingService.findVacantDays(startDate, endDate);
//    // then
//    // IllegalArgumentException is thrown
//  }
//
//  @Test
//  public void findVacantDates_bookingDatesOverlapRangeDates_noVacantDates() {
//    // given: -S|-|----|-|E-
//    LocalDate startDate = LocalDate.now().plusDays(2);
//    LocalDate endDate = LocalDate.now().plusDays(3);
//    Booking booking = BookingMapper.INSTANCE.toBooking(
//        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)));
//    doReturn(Lists.newArrayList(booking))
//        .when(bookingRepository).findForDateRange(startDate, endDate);
//    // then
//    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
//    // when
//    assertThat(vacantDates).isEmpty();
//  }
//
//  @Test
//  public void findVacantDates_bookingDatesSameAsRangeDates_vacantRangeEndDate() {
//    // given: --|S|----|E|--
//    LocalDate startDate = LocalDate.now().plusDays(1);
//    LocalDate endDate = LocalDate.now().plusDays(4);
//    Booking booking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(startDate, endDate));
//    doReturn(Lists.newArrayList(booking))
//        .when(bookingRepository).findForDateRange(startDate, endDate);
//    // then
//    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
//    // when
//    assertThat(vacantDates).size().isEqualTo(1);
//    assertThat(vacantDates).contains(endDate);
//  }
//
//  @Test
//  public void findVacantDates_noBookingsFound_vacantDatesWithinDateRangeInclusive() {
//    // given: --|-|----|-|--
//    LocalDate startDate = LocalDate.now().plusDays(1);
//    LocalDate endDate = LocalDate.now().plusDays(4);
//    doReturn(Lists.newArrayList())
//        .when(bookingRepository).findForDateRange(startDate, endDate);
//    // when
//    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
//    // then
//    List<LocalDate> expected = startDate
//        .datesUntil(endDate.plusDays(1))
//        .collect(Collectors.toList());
//    assertThat(vacantDates).isEqualTo(expected);
//  }
//
//
//  @Test
//  public void updateBooking_bookingIsCancelled_illegalBookingStateExceptionThrown() {
//    exception.expect(IllegalBookingStateException.class);
//    // given
//    UUID uuid = UUID.randomUUID();
//    Booking booking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1), uuid));
//
//    Booking persistedBooking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1), uuid));
//    persistedBooking.setActive(false);
//    doReturn(Optional.of(persistedBooking)).when(bookingRepository).findByUuid(uuid);
//    // when
//    bookingService.updateBooking(booking);
//    // then
//    // IllegalBookingStateException thrown
//  }
//
//  @Test
//  public void updateBooking_bookingDatesNotAvailable_bookingDatesNotAvailableExceptionThrown() {
//    exception.expect(BookingDatesNotAvailableException.class);
//    // given
//    UUID uuid = UUID.randomUUID();
//    Booking booking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(3), LocalDate.now().plusDays(1), uuid));
//    booking.setUuid(uuid);
//
//    Booking persistedBooking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1), uuid));
//    doReturn(Optional.of(persistedBooking)).when(bookingRepository).findByUuid(uuid);
//
//    Booking otherBooking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(3), LocalDate.now().plusDays(2), UUID.randomUUID()));
//    doReturn(Lists.newArrayList(persistedBooking, otherBooking))
//        .when(bookingRepository).findForDateRange(booking.getStartDate(), booking.getEndDate());
//    // when
//    bookingService.updateBooking(booking);
//    // then
//    // IllegalBookingStateException thrown
//  }
//
//  @Test
//  public void updateBooking_bookingDatesAvailable_bookingUpdated() {
//    // given
//    UUID uuid = UUID.randomUUID();
//    Booking booking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(3), LocalDate.now().plusDays(1), uuid));
//
//    Booking persistedBooking = BookingMapper.INSTANCE.toBooking(helper.buildBooking(
//        LocalDate.now().plusDays(2), LocalDate.now().plusDays(1), uuid));
//    doReturn(Optional.of(persistedBooking)).when(bookingRepository).findByUuid(uuid);
//
//    doReturn(Lists.newArrayList(persistedBooking))
//        .when(bookingRepository).findForDateRange(booking.getStartDate(), booking.getEndDate());
//    // when
//    bookingService.updateBooking(booking);
//    // then
//    verify(bookingRepository, times(1)).save(booking);
//  }

}