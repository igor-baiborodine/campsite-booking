package com.kiroule.campsitebooking.service;

import static java.time.LocalDate.now;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kiroule.campsitebooking.BaseIT;
import com.kiroule.campsitebooking.TestDataHelper;
import com.kiroule.campsitebooking.mapper.BookingMapper;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import com.kiroule.campsitebooking.repository.entity.BookingEntity;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Concurrent integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
class BookingServiceImplConcurrentIT extends BaseIT {

  @Autowired
  @Qualifier("bookingService")
  BookingService classUnderTest;

  @Autowired BookingRepository bookingRepository;

  @Autowired
  BookingMapper bookingMapper;

  @Autowired
  TestDataHelper testDataHelper;

  @Nested
  class CreateBooking {

    @Test
    @SneakyThrows
    void given_3_new_bookings_with_same_booking_dates__then_only_1_created() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      List<Booking> bookings =
          newArrayList(
              buildBooking(campsiteEntity.getId()),
              buildBooking(campsiteEntity.getId()),
              buildBooking(campsiteEntity.getId()));
      // when
      ExecutorService executor = newFixedThreadPool(bookings.size());
      bookings.forEach(b -> executor.execute(() -> classUnderTest.createBooking(b)));
      executor.shutdown();
      // then
      assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

      List<BookingEntity> bookingEntities =
          bookingRepository.findForDateRange(
              bookings.get(0).getStartDate(), bookings.get(0).getEndDate(), campsiteEntity.getId());
      assertThat(bookingEntities).hasSize(1);

      BookingEntity createdBookingEntity = bookingEntities.get(0);
      assertThat(createdBookingEntity).hasNoNullFieldsOrProperties();

      Predicate<Booking> bookingPredicate =
          b ->
              b.getCampsiteId().equals(createdBookingEntity.getCampsiteId())
                  && b.getStartDate().equals(createdBookingEntity.getStartDate())
                  && b.getEndDate().equals(createdBookingEntity.getEndDate())
                  && b.getEmail().equals(createdBookingEntity.getEmail())
                  && b.getFullName().equals(createdBookingEntity.getFullName());
      List<Booking> filteredBookings = bookings.stream().filter(bookingPredicate).toList();
      assertThat(filteredBookings).hasSize(1);
    }

    private Booking buildBooking(Long campsiteId) {
      return TestDataHelper.nextBooking().toBuilder()
          .id(null)
          .uuid(null)
          .version(null)
          .campsiteId(campsiteId)
          .startDate(now().plusDays(1))
          .endDate(now().plusDays(2))
          .createdAt(null)
          .updatedAt(null)
          .build();
    }
  }

  @Nested
  class UpdateBooking {

    List<Booking> existingBookings;
    List<Booking> existingBookingUpdates;

    @BeforeEach
    void beforeEach() {
      existingBookings = new ArrayList<>();
      existingBookingUpdates = new ArrayList<>();
    }

    @Test
    @SneakyThrows
    void given_2_updates_for_same_existing_booking__then_only_1_update_executed() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      List<Booking> bookingUpdates =
          List.of(
              buildBookingUpdate(bookingEntity, 1, 3), //
              buildBookingUpdate(bookingEntity, 2, 4));
      // when
      ExecutorService executor = newFixedThreadPool(bookingUpdates.size());
      bookingUpdates.forEach(b -> executor.execute(() -> classUnderTest.updateBooking(b)));
      executor.shutdown();
      // then
      assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

      Optional<BookingEntity> updatedBookingEntity =
          bookingRepository.findByUuid(bookingEntity.getUuid());
      assertThat(updatedBookingEntity).isPresent();
      assertThat(updatedBookingEntity.get().getVersion()).isEqualTo(1L);

      Predicate<Booking> bookingPredicate =
          b ->
              b.getCampsiteId().equals(updatedBookingEntity.get().getCampsiteId())
                  && b.getStartDate().equals(updatedBookingEntity.get().getStartDate())
                  && b.getEndDate().equals(updatedBookingEntity.get().getEndDate());
      List<Booking> filteredBookings = bookingUpdates.stream().filter(bookingPredicate).toList();
      assertThat(filteredBookings).hasSize(1);
    }

    @Test
    @SneakyThrows
    void given_3_updated_bookings_with_same_booking_dates__then_only_1_updated() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity1 =
          testDataHelper.createBookingEntity(campsiteEntity.getId(), 1, 2);
      BookingEntity bookingEntity2 =
          testDataHelper.createBookingEntity(campsiteEntity.getId(), 2, 3);
      BookingEntity bookingEntity3 =
          testDataHelper.createBookingEntity(campsiteEntity.getId(), 3, 4);

      LocalDate newStartDate = now();
      LocalDate newEndDate = newStartDate.plusDays(1);
      List<Booking> bookingUpdates =
          List.of(
              buildBookingUpdate(bookingEntity1, newStartDate, newEndDate), //
              buildBookingUpdate(bookingEntity2, newStartDate, newEndDate), //
              buildBookingUpdate(bookingEntity3, newStartDate, newEndDate));
      // when
      ExecutorService executor = newFixedThreadPool(bookingUpdates.size());
      bookingUpdates.forEach(b -> executor.execute(() -> classUnderTest.updateBooking(b)));
      executor.shutdown();
      // then
      assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

      List<BookingEntity> bookingEntities =
          bookingRepository.findForDateRange(newStartDate, newEndDate, campsiteEntity.getId());
      assertThat(bookingEntities).hasSize(1);

      BookingEntity updatedBookingEntity = bookingEntities.get(0);
      assertThat(updatedBookingEntity.getVersion()).isEqualTo(1L);

      List<Booking> filteredBookingUpdates =
          bookingUpdates.stream()
              .filter(b -> !b.getUuid().equals(updatedBookingEntity.getUuid()))
              .toList();
      assertThat(filteredBookingUpdates).hasSize(2);

      filteredBookingUpdates.stream()
          .map(b -> bookingRepository.findByUuid(b.getUuid()))
          .forEach(e -> assertThat(e.get().getVersion()).isZero());
    }

    private Booking buildBookingUpdate(
        BookingEntity bookingEntity, int startDateDaysToAdd, int endDateDaysToAdd) {
      return bookingMapper.toBooking(bookingEntity).toBuilder()
          .startDate(bookingEntity.getStartDate().plusDays(startDateDaysToAdd))
          .endDate(bookingEntity.getEndDate().plusDays(endDateDaysToAdd))
          .build();
    }

    private Booking buildBookingUpdate(
        BookingEntity bookingEntity, LocalDate startDate, LocalDate endDate) {
      return bookingMapper.toBooking(bookingEntity).toBuilder()
          .startDate(startDate)
          .endDate(endDate)
          .build();
    }
  }
}
