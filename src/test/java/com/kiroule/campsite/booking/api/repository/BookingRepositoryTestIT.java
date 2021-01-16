package com.kiroule.campsite.booking.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.TestHelper;
import com.kiroule.campsite.booking.api.model.mapper.BookingMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("h2")
@Transactional
public class BookingRepositoryTestIT {

  @Autowired
  private TestHelper helper;

  @Autowired
  private BookingRepository bookingRepository;

  @Before
  public void setUp() {
    bookingRepository.deleteAll();
  }

  @Test
  public void findById_savedBooking_savedBookingFound() {
    // given
    Booking savedBooking = bookingRepository.save(
        BookingMapper.INSTANCE.toBooking(
            helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
    // when
    Optional<Booking> foundBooking = bookingRepository.findById(savedBooking.getId());
    // then
    assertThat(foundBooking).hasValue(savedBooking);
  }

  @Test
  public void findByUuid_savedBooking_savedBookingFound() {
    // given
    UUID uuid = UUID.randomUUID();
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(uuid, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
    // when
    Optional<Booking> foundBooking = bookingRepository.findByUuid(uuid);
    // then
    assertThat(foundBooking).hasValue(savedBooking);
  }

  @Test
  public void findByUuid_savedBooking_savedBookingHasCreatedAt() {
    // given
    UUID uuid = UUID.randomUUID();
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(uuid, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
    // when
    Optional<Booking> foundBooking = bookingRepository.findByUuid(uuid);
    // then
    assertThat(foundBooking).hasValue(savedBooking);
    assertThat(foundBooking.get().getCreatedAt()).isNotNull();
  }

  @Test
  public void findForDateRange_bookingDatesBeforeRangeStartDate_noBookingFound() {
    // given: -S-E|-|----|-|--
    bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(3), LocalDate.now().plusDays(4));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void
      findForDateRange_bookingStartBeforeRangeStartDateAndBookingEndDateEqualsToRangeStartDate_noBookingFound() {
    // given: -S|E|----|-|--
    bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingStartDateBeforeRangeStartDateAndBookingEndDateWithinRangeDates_bookingFound() {
    // given: -S|-|E---|-|--
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(2), LocalDate.now().plusDays(4))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(3), LocalDate.now().plusDays(4))));
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
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(3), LocalDate.now().plusDays(4))));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
    // then
    assertThat(bookings).isEmpty();
  }

  @Test
  public void findForDateRange_bookingDatesOverlapRangeDates_bookingFound() {
    // given: -S|-|----|-|E-
    Booking savedBooking = bookingRepository.save(BookingMapper.INSTANCE.toBooking(
        helper.buildBooking(LocalDate.now().plusDays(1), LocalDate.now().plusDays(4))));
    // when
    List<Booking> bookings = bookingRepository.findForDateRange(
        LocalDate.now().plusDays(2), LocalDate.now().plusDays(3));
    // then
    assertThat(bookings).size().isEqualTo(1);
    assertThat(savedBooking).isIn(bookings);
  }

}
