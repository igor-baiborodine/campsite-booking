package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.assertj.core.api.Fail.fail;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kiroule.campsite.booking.api.BaseIT;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.mapper.CampsiteMapper;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Concurrent integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@Disabled
class BookingServiceImplConcurrentIT extends BaseIT {

  @Autowired BookingRepository bookingRepository;

  @Autowired
  @Qualifier("bookingService")
  BookingService classUnderTest;

  @Autowired CampsiteRepository campsiteRepository;

  @Autowired CampsiteMapper campsiteMapper;

  @Autowired BookingMapper bookingMapper;

  ExecutorService executor;
  LocalDate now;

  @BeforeEach
  void tearDown() {
    bookingRepository.deleteAll();
    now = LocalDate.now();
  }

  @SneakyThrows
  private void then_assertExecutorTerminated() {
    assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));
  }

  @Nested
  class CreateBooking {

    List<Booking> newBookings;

    @BeforeEach
    void beforeEach() {
      newBookings = null;
    }

    @Test
    void given_3_new_bookings_with_same_booking_dates__then_only_1_created() {
      given_threeBookingsWithSameBookingDates(1, 2);

      when_createBookingsConcurrently();

      then_assertExecutorTerminated();
      then_assertCreatedBooking();
    }

    private void given_threeBookingsWithSameBookingDates(int startPlusDays, int endPlusDays) {
      Campsite campsite = campsiteMapper.toCampsite(campsiteRepository.findById(CAMPSITE_ID).get());
      newBookings =
          asList(
              buildBooking(
                  now.plusDays(startPlusDays),
                  now.plusDays(endPlusDays),
                  UUID.randomUUID(),
                  CAMPSITE_ID),
              buildBooking(
                  now.plusDays(startPlusDays),
                  now.plusDays(endPlusDays),
                  UUID.randomUUID(),
                  CAMPSITE_ID),
              buildBooking(
                  now.plusDays(startPlusDays),
                  now.plusDays(endPlusDays),
                  UUID.randomUUID(),
                  CAMPSITE_ID));
    }

    private void when_createBookingsConcurrently() {
      executor = Executors.newFixedThreadPool(newBookings.size());
      newBookings.forEach(b -> executor.execute(() -> classUnderTest.createBooking(b)));
      executor.shutdown();
    }

    private void then_assertCreatedBooking() {
      List<Booking> bookings =
          newArrayList(bookingRepository.findAll()).stream()
              .map(b -> bookingMapper.toBooking(b))
              .toList();
      assertThat(bookings).hasSize(1);

      UUID uuid = bookings.iterator().next().getUuid();
      // To avoid org.hibernate.LazyInitializationException for campsite field,
      // use BookingService to fetch booking in question
      Booking createdBooking = classUnderTest.findByUuid(uuid);

      Booking newBooking =
          newBookings.stream().filter(b -> b.getUuid().equals(uuid)).findFirst().get();
      assertThat(createdBooking)
          .usingRecursiveComparison()
          .ignoringFields("id", "version", "createdAt", "updatedAt", "campsite")
          .isEqualTo(newBooking);
      //      assertThat(createdBooking.getCampsite())
      //          .usingRecursiveComparison()
      //          .ignoringFields("$$_hibernate_interceptor")
      //          .isEqualTo(newBooking.getCampsite());
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
    void given_2_updates_for_same_existing_booking__then_only_1_update_executed() {
      given_existingBooking(1, 2, UUID.randomUUID());
      given_twoUpdatesForExistingBooking();

      when_updateBookingConcurrently();

      then_assertExecutorTerminated();
      then_assertOptimisticLockingBookingUpdate();
    }

    @Test
    void given_3_updated_bookings_with_same_booking_dates__then_only_1_updated() {
      given_existingBooking(1, 2, UUID.randomUUID());
      given_existingBooking(3, 4, UUID.randomUUID());
      given_existingBooking(5, 6, UUID.randomUUID());
      given_sameBookingDatesUpdateForExistingBookings(7, 8);

      when_updateBookingConcurrently();

      then_assertExecutorTerminated();
      then_assertPessimisticLockingBookingUpdate(7, 8);
    }

    private void given_existingBooking(int startPlusDays, int endPlusDays, UUID uuid) {
      Booking booking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
      BookingEntity bookingEntity = bookingMapper.toBookingEntity(booking);
      Booking savedBooking = bookingMapper.toBooking(bookingRepository.save(bookingEntity));
      existingBookings.add(savedBooking);

      //      assumeThat(savedBooking.isNew()).isFalse();
      assumeThat(savedBooking.isActive()).isTrue();
      assumeThat(savedBooking.getVersion()).isEqualTo(0L);
    }

    private void given_twoUpdatesForExistingBooking() {
      existingBookingUpdates = asList(updateBooking(0, 3, 5), updateBooking(0, 13, 15));
    }

    private void given_sameBookingDatesUpdateForExistingBookings(
        int startPlusDays, int endPlusDays) {
      existingBookingUpdates =
          asList(
              updateBooking(0, startPlusDays, endPlusDays),
              updateBooking(1, startPlusDays, endPlusDays),
              updateBooking(2, startPlusDays, endPlusDays));
    }

    private Booking updateBooking(int index, int startPlusDays, int endPlusDays) {
      Booking booking = existingBookings.get(index);

      Booking updatedBooking =
          booking.toBuilder()
              .startDate(now.plusDays(startPlusDays))
              .endDate(now.plusDays(endPlusDays))
              .build();
      updatedBooking.setCreatedAt(booking.getCreatedAt());
      updatedBooking.setUpdatedAt(booking.getUpdatedAt());

      return updatedBooking;
    }

    private void when_updateBookingConcurrently() {
      executor = Executors.newFixedThreadPool(existingBookingUpdates.size());
      existingBookingUpdates.forEach(b -> executor.execute(() -> classUnderTest.updateBooking(b)));
      executor.shutdown();
    }

    private void then_assertOptimisticLockingBookingUpdate() {
      List<Booking> bookings =
          newArrayList(bookingRepository.findAll()).stream()
              .map(b -> bookingMapper.toBooking(b))
              .toList();
      assertThat(bookings).hasSize(1);

      Booking updatedBooking = bookings.get(0);
      assertThat(updatedBooking.getVersion()).isEqualTo(1L);
      assertThat(updatedBooking.getStartDate())
          .isNotEqualTo(existingBookings.get(0).getStartDate());
      assertThat(updatedBooking.getEndDate()).isNotEqualTo(existingBookings.get(0).getEndDate());
    }

    private void then_assertPessimisticLockingBookingUpdate(int startPlusDays, int endPlusDays) {
      List<BookingEntity> bookings = newArrayList(bookingRepository.findAll());
      assertThat(bookings).hasSize(3);

      bookings.forEach(
          b -> {
            if (b.getVersion() == 0L) {
              assertThat(b.getStartDate()).isNotEqualTo(now.plusDays(startPlusDays));
              assertThat(b.getEndDate()).isNotEqualTo(now.plusDays(endPlusDays));
            } else if (b.getVersion() == 1L) {
              assertThat(b.getStartDate()).isEqualTo(now.plusDays(startPlusDays));
              assertThat(b.getEndDate()).isEqualTo(now.plusDays(endPlusDays));
            } else {
              fail("Illegal version value for %s", b);
            }
          });
    }
  }
}
