package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("hsqldb")
public class BookingRepositoryIntegrationTest {

  @Autowired
  private BookingRepository repository;

  @Test
  public void findById_savedBooking_savedBookingFound() {
    // given
    Booking savedBooking = repository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    Optional<Booking> foundBooking = repository.findById(savedBooking.getId());
    // then
    Assertions.assertThat(foundBooking).hasValue(savedBooking);
  }

  @Test
  public void findForDateRange_bookingEndDateBeforeRangeStartDate_noBookingFound() {
    // given
    Booking savedBooking = repository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));
    // when
    List<Booking> bookings = repository.findForDateRange(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4));
    // then
    Assertions.assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingEndDateEqualsRangeStartDate_noBookingFound() {
    // given
    Booking savedBooking = repository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3)));
    // when
    List<Booking> bookings = repository.findForDateRange(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4));
    // then
    Assertions.assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingEndDateAfterRangeStartDate_oneBookingFound() {
    // given
    Booking savedBooking = repository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 3)));
    // when
    List<Booking> bookings = repository.findForDateRange(
        LocalDate.of(2018, 10, 2),
        LocalDate.of(2018, 10, 4));
    // then
    Assertions.assertThat(bookings).size().isEqualTo(1);
    Assertions.assertThat(savedBooking).isIn(bookings);
  }

  private Booking createBooking(LocalDate startDate, LocalDate endDate) {
    return Booking.builder()
        .fullName("John Smith")
        .email("john.smith@domain.com")
        .startDate(startDate)
        .endDate(endDate)
        .active(true)
        .build();
  }
}
