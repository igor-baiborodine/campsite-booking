package com.kiroule.campsitebooking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsitebooking.TestHelper;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookingRepositoryTestIT extends TestHelper {

  @Autowired
  private BookingRepository bookingRepository;

  @Before
  public void setUp() {
    bookingRepository.deleteAll();
  }

  @Test
  public void findForDateRange_bookingStartDateBeforeRangeStartDateAndBookingEndDateWithinRangeDates_bookingFound() {
    // given: -S|-|E---|-|--
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateEqualsToRangeStartDateAndBookingEndDateWithinRangeDates_bookingFound() {
    // given: --|S|E---|-|--
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingDatesWithinRangeDates_bookingFound() {
    // given: --|-|S--E|-|--
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange__startBookingDateWithinRangeDatesAndBookingEndDateEqualsToRangeEndDate_bookingFound() {
    // given: --|-|---S|E|--
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateBeforeRangeEndDateAndBookingEndDateAfterRangeEndDate_bookingFound() {
    // given: --|-|---S|-|E-
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateEqualsToRangeEndDateAndBookingEndDateAfterRangeEndDate_bookingFound() {
    // given: --|-|----|S|E-
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(3), LocalDate.now().plusDays(4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingDatesAfterRangeEndDate_noBookingFound() {
    // given: --|-|----|-|S-E-
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(3), LocalDate.now().plusDays(4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingDatesOverlapRangeDates_bookingFound() {
    // given: -S|-|----|-|E-
    Booking savedBooking = bookingRepository.save(buildBooking(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

}
