package com.kiroule.campsitebooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Lists;
import com.kiroule.campsitebooking.AbstractTest;
import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.exception.IllegalBookingStateException;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest extends AbstractTest {

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private BookingServiceImpl bookingService;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void findBookingById_nonExistingBookingId_bookingNotFoundExceptionThrown()
      throws BookingNotFoundException {
    exception.expect(BookingNotFoundException.class);
    // given
    long id = 1L;
    doReturn(Optional.empty()).when(bookingRepository).findById(id);
    // when
    bookingService.findBookingById(id);
    // then
    // BookingNotFoundException is thrown
  }

  @Test
  public void findBookingById_existingBookingId_bookingFound() throws BookingNotFoundException {
    // given
    long id = 1L;
    Booking savedBooking = createBooking(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2));
    doReturn(Optional.of(savedBooking)).when(bookingRepository).findById(id);
    // when
    Booking booking = bookingService.findBookingById(id);
    // then
    assertThat(booking).isEqualTo(savedBooking);
  }

  @Test
  public void checkBookingAvailability_bookingDatesOverlapRangeDates_noVacantDates() {
    // given: -S|-|----|-|E-
    LocalDate startDate = LocalDate.of(2018, 10, 2);
    LocalDate endDate = LocalDate.of(2018, 10, 3);
    doReturn(
        Lists.newArrayList(createBooking(LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 4))))
        .when(bookingRepository).findForDateRange(startDate, endDate);
    // then
    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
    // when
    assertThat(vacantDates).isEmpty();
  }

  @Test
  public void checkBookingAvailability_bookingDatesSameAsRangeDates_vacantRangeEndDate() {
    // given: --|S|----|E|--
    LocalDate startDate = LocalDate.of(2018, 10, 1);
    LocalDate endDate = LocalDate.of(2018, 10, 4);
    doReturn(
        Lists.newArrayList(createBooking(startDate, endDate)))
        .when(bookingRepository).findForDateRange(startDate, endDate);
    // then
    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
    // when
    assertThat(vacantDates).size().isEqualTo(1);
    assertThat(vacantDates).contains(endDate);
  }

  @Test
  public void checkBookingAvailability_noBookingsFound_vacantDatesWithinDateRangeInclusive() {
    // given: --|-|----|-|--
    LocalDate startDate = LocalDate.of(2018, 10, 1);
    LocalDate endDate = LocalDate.of(2018, 10, 4);
    doReturn(Lists.newArrayList())
        .when(bookingRepository).findForDateRange(startDate, endDate);
    // when
    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
    // then
    List<LocalDate> expected = startDate
        .datesUntil(endDate.plusDays(1))
        .collect(Collectors.toList());
    assertThat(vacantDates).isEqualTo(expected);
  }

  @Test
  public void createBooking_bookingIsNotNew_illegalBookingStateExceptionThrown()
      throws BookingDatesNotAvailableException, IllegalBookingStateException {
    exception.expect(IllegalBookingStateException.class);
    // given
    Booking booking = createBooking(LocalDate.now(), LocalDate.now().plusDays(1));
    booking.setId(1L);
    // when
    bookingService.createBooking(booking);
    // then
    // IllegalBookingStateException thrown
  }

  @Test
  public void createBooking_bookingDatesNotAvailable_bookingDatesNotAvailableExceptionThrown()
      throws BookingDatesNotAvailableException, IllegalBookingStateException {
    exception.expect(BookingDatesNotAvailableException.class);
    // given
    Booking booking = createBooking(LocalDate.now(), LocalDate.now().plusDays(3));
    doReturn(Lists.newArrayList(createBooking(LocalDate.now(), LocalDate.now().plusDays(1))))
        .when(bookingRepository).findForDateRange(booking.getStartDate(), booking.getEndDate());
    // when
    bookingService.createBooking(booking);
    // then
    // BookingDatesNotAvailableException thrown
  }

  @Test
  public void createBooking_bookingDatesAvailable_bookingCreated()
      throws BookingDatesNotAvailableException, IllegalBookingStateException {
    // given
    Booking booking = createBooking(LocalDate.now(), LocalDate.now().plusDays(3));
    doReturn(Lists.newArrayList())
        .when(bookingRepository).findForDateRange(booking.getStartDate(), booking.getEndDate());
    // when
    bookingService.createBooking(booking);
    // then
    verify(bookingRepository, times(1)).save(booking);
  }

}