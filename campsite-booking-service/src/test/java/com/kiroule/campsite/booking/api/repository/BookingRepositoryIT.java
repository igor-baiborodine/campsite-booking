package com.kiroule.campsite.booking.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.BaseIT;
import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.TestDataHelper;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@Transactional
class BookingRepositoryIT extends BaseIT {

  @Autowired BookingRepository classUnderTest;

  @Autowired CampsiteRepository campsiteRepository;

  @Autowired BookingMapper bookingMapper;

  @Autowired TestDataHelper testDataGenerator;

  @Nested
  class FindByUuid {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity = testDataGenerator.createBookingEntity(campsiteEntity.getId());
      // when
      Optional<BookingEntity> result = classUnderTest.findByUuid(bookingEntity.getUuid());
      // then
      assertThat(result).hasValue(bookingEntity);
    }
  }

  @Nested
  class FindForDateRange {

    @Test
    @DisplayNamePrefix("SE|-|----|-|--")
    void given_booking_dates_before_range_start_date__then_no_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 1, 2);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(3),
              bookingEntity.getStartDate().plusDays(4),
              campsiteEntity.getId());
      // then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayNamePrefix("-S|E|----|-|--")
    void
        given_booking_start_date_before_range_start_date_and_booking_end_date_equals_to_range_start_date__then_no_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 1, 2);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(2),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayNamePrefix("-S|-|E---|-|--")
    void
        given_booking_start_date_before_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 1, 3);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(2),
              bookingEntity.getStartDate().plusDays(4),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|S|E---|-|--")
    void
        given_booking_start_date_equals_to_range_start_date_and_booking_end_date_within_range_dates__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 1, 2);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|-|S--E|-|--")
    void given_booking_dates_within_range_dates__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 2, 3);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(4),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|-|---S|E|--")
    void
        given_booking_start_date_within_range_dates_and_booking_end_date_equals_to_range_end_date__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 2, 3);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|-|---S|-|E-")
    void
        given_booking_start_date_before_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 2, 4);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|-|----|S|E-")
    void
        given_booking_start_date_equals_to_range_end_date_and_booking_end_date_after_range_end_date__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 3, 4);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("--|-|----|-|SE")
    void given_booking_dates_after_range_end_date__then_no_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 3, 4);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(1),
              bookingEntity.getStartDate().plusDays(2),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }

    @Test
    @DisplayNamePrefix("-S|-|----|-|E-")
    void given_booking_dates_overlap_range_dates__then_booking_found() {
      // given
      CampsiteEntity campsiteEntity = testDataGenerator.createCampsiteEntity();
      BookingEntity bookingEntity =
          testDataGenerator.createBookingEntity(campsiteEntity.getId(), 1, 4);
      // when
      List<BookingEntity> result =
          classUnderTest.findForDateRange(
              bookingEntity.getStartDate().plusDays(2),
              bookingEntity.getStartDate().plusDays(3),
              campsiteEntity.getId());
      // then
      assertThat(result).hasSize(1).contains(bookingEntity);
    }
  }
}
