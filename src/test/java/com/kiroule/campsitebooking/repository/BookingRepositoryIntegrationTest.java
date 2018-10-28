package com.kiroule.campsitebooking.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsitebooking.AbstractTest;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@Transactional
@ActiveProfiles("hsqldb")
public class BookingRepositoryIntegrationTest extends AbstractTest {

  @Autowired
  private BookingRepository bookingRepository;

  @Test
  public void findById_savedBooking_savedBookingFound() {
    // given
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());
    // then
    assertThat(foundBooking).hasValue(savedBooking);
  }

  @Test
  public void findForDateRange_bookingDatesBeforeRangeStartDate_noBookingFound() {
    // given: -S-E|-|----|-|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange__bookingStartBeforeRangeStartDateAndBookingEndDateEqualsToRangeStartDate_noBookingFound() {
    // given: -S|E|----|-|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingStartDateBeforeRangeStardDateAndBookingEndDateWithinRangeDates_bookingFound() {
    // given: -S|-|E---|-|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 4));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateEqualsToRangeStartDateAndBookingEndDateWithinRangeDates_bookingFound() {
    // given: --|S|E---|-|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingDatesWithinRangeDates_bookingFound() {
    // given: --|-|S--E|-|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 4));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange__startBookingDateWithinRangeDatesAndBookingEndDateEqualsToRangeEndDate_bookingFound() {
    // given: --|-|---S|E|--
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 3)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateBeforeRangeEndDateAndBookingEndDateAfterRangeEndDate_bookingFound() {
    // given: --|-|---S|-|E-
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange_bookingStartDateEqualsToRangeEndDateAndBookingEndDateAfterRangeEndDate_bookingFound() {
    // given: --|-|----|S|E-
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

  @Test
  public void findForDateRange__bookingDatesAfterRangeEndDate_noBookingFound() {
    // given: --|-|----|-|S-E-
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingDatesOverlapRangeDates_bookingFound() {
    // given: -S|-|----|-|E-
    Booking savedBooking = bookingRepository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 4)));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

}
