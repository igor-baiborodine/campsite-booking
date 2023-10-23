package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestDataHelper.*;
import static java.time.LocalDate.now;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kiroule.campsite.booking.api.DisplayNamePrefix;
import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

  @Mock BookingRepository bookingRepository;

  @Mock BookingMapper bookingMapper;

  @InjectMocks BookingServiceImpl classUnderTest;

  @Nested
  class FindBookingByUuid {

    @Test
    void happy_path() {
      // given
      Booking booking = nextBooking();
      BookingEntity bookingEntity = nextBookingEntity();
      doReturn(Optional.of(bookingEntity)).when(bookingRepository).findByUuid(any());
      doReturn(booking).when(bookingMapper).toBooking(any(BookingEntity.class));
      // when
      Booking result = classUnderTest.findByUuid(booking.getUuid());
      // then
      assertThat(result).isEqualTo(booking);
      verify(bookingRepository).findByUuid(booking.getUuid());
      verify(bookingMapper).toBooking(bookingEntity);
    }

    @Test
    void given_non_existing_booking_uuid__then_BookingNotFoundException_thrown() {
      // given
      Booking booking = nextBooking();
      // when
      Executable executable = () -> classUnderTest.findByUuid(booking.getUuid());
      // then
      assertThrows(BookingNotFoundException.class, executable);
      verify(bookingRepository).findByUuid(booking.getUuid());
      verify(bookingMapper, never()).toBooking(any(BookingEntity.class));
    }
  }

  @Nested
  class CreateBooking {

    @Test
    void happy_path() {
      Booking booking = nextBooking().toBuilder().uuid(null).version(null).build();
      BookingEntity bookingEntity = nextBookingEntity();

      doReturn(emptyList())
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
      doReturn(bookingEntity).when(bookingRepository).saveAndFlush(any());
      doReturn(bookingEntity).when(bookingMapper).toBookingEntity(any());
      doReturn(booking).when(bookingMapper).toBooking(any(BookingEntity.class));
      // when
      Booking result = classUnderTest.createBooking(booking);
      // then
      assertThat(result).isEqualTo(booking);
      verify(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(
              booking.getStartDate(), booking.getEndDate(), booking.getCampsiteId());
      verify(bookingRepository).saveAndFlush(bookingEntity);
      verify(bookingMapper).toBookingEntity(booking);
      verify(bookingMapper).toBooking(bookingEntity);
    }

    @Test
    void given_booking_dates_not_available__then_BookingDatesNotAvailableException_thrown() {
      // given
      Booking booking = nextBooking().toBuilder().uuid(null).version(null).build();
      Booking existingBooking =
          nextBooking().toBuilder()
              .startDate(booking.getStartDate())
              .endDate(booking.getEndDate())
              .build();
      BookingEntity existingBookingEntity = nextBookingEntity();
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
      doReturn(singletonList(existingBooking)).when(bookingMapper).toBookingsList(any());
      // when
      Executable executable = () -> classUnderTest.createBooking(booking);
      // then
      assertThrows(BookingDatesNotAvailableException.class, executable);
      verify(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(
              booking.getStartDate(), booking.getEndDate(), booking.getCampsiteId());
      verify(bookingMapper).toBookingsList(singletonList(existingBookingEntity));
    }

    @Test
    void given_new_booking_has_uuid__then_IllegalArgumentException_thrown() {
      // given
      Booking booking = nextBooking();
      // when
      Executable executable = () -> classUnderTest.createBooking(booking);
      // then
      assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void given_new_booking_not_active__then_IllegalArgumentException_thrown() {
      // given
      Booking booking = nextBooking().toBuilder().active(false).build();
      // when
      Executable executable = () -> classUnderTest.createBooking(booking);
      // then
      assertThrows(IllegalArgumentException.class, executable);
    }
  }

  @Nested
  class FindVacantDates {

    private static final Long CAMPSITE_ID = 1L;

    @Test
    @DisplayNamePrefix("--|-|----|-|--")
    void happy_path() {
      // given
      LocalDate startDate = now().plusDays(1);
      LocalDate endDate = now().plusDays(4);
      List<LocalDate> bookingDates = startDate.datesUntil(endDate.plusDays(1)).toList();
      doReturn(emptyList()).when(bookingRepository).findForDateRange(any(), any(), any());
      // when
      List<LocalDate> vacantDates = classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // then
      assertThat(vacantDates).hasSize(bookingDates.size()).containsAll(bookingDates);
      verify(bookingRepository).findForDateRange(startDate, endDate, CAMPSITE_ID);
    }

    @Test
    void given_range_start_date_is_now__then_IllegalArgumentException_thrown() {
      // given
      LocalDate startDate = now().plusDays(0);
      LocalDate endDate = now().plusDays(2);
      // then
      Executable executable = () -> classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // when
      assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void given_range_end_date_is_now__then_IllegalArgumentException_thrown() {
      // given
      LocalDate startDate = now().plusDays(2);
      LocalDate endDate = now().plusDays(0);
      // then
      Executable executable = () -> classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // when
      assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void given_range_end_date_is_before_range_start_date__then_IllegalArgumentException_thrown() {
      // given
      LocalDate startDate = now().plusDays(3);
      LocalDate endDate = now().plusDays(1);
      // then
      Executable executable = () -> classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // when
      assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    @DisplayNamePrefix("-S|-|----|-|E-")
    void given_booking_dates_overlap_range_dates__then_no_vacant_dates_found() {
      // given
      LocalDate startDate = now().plusDays(2);
      LocalDate endDate = now().plusDays(3);
      LocalDate existingStartDate = startDate.minusDays(1);
      LocalDate existingEndDate = endDate.plusDays(1);

      Booking existingBooking =
          nextBooking().toBuilder().startDate(existingStartDate).endDate(existingEndDate).build();
      BookingEntity existingBookingEntity =
          nextBookingEntity().toBuilder()
              .startDate(existingStartDate)
              .endDate(existingEndDate)
              .build();
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRange(any(), any(), any());
      doReturn(singletonList(existingBooking)).when(bookingMapper).toBookingsList(any());
      // when
      List<LocalDate> vacantDates = classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // then
      assertThat(vacantDates).isEmpty();
      verify(bookingRepository).findForDateRange(startDate, endDate, CAMPSITE_ID);
      verify(bookingMapper).toBookingsList(singletonList(existingBookingEntity));
    }

    @Test
    @DisplayNamePrefix("--|S|----|E|--")
    void given_booking_dates_same_as_range_dates__then_end_date_found() {
      // given
      LocalDate startDate = now().plusDays(1);
      LocalDate endDate = now().plusDays(4);

      Booking existingBooking =
          nextBooking().toBuilder().startDate(startDate).endDate(endDate).build();
      BookingEntity existingBookingEntity =
          nextBookingEntity().toBuilder().startDate(startDate).endDate(endDate).build();
      doReturn(singletonList(existingBookingEntity))
          .when(bookingRepository)
          .findForDateRange(any(), any(), any());
      doReturn(singletonList(existingBooking)).when(bookingMapper).toBookingsList(any());
      // when
      List<LocalDate> vacantDates = classUnderTest.findVacantDates(startDate, endDate, CAMPSITE_ID);
      // then
      assertThat(vacantDates).hasSize(1).contains(endDate);
      verify(bookingRepository).findForDateRange(startDate, endDate, CAMPSITE_ID);
      verify(bookingMapper).toBookingsList(singletonList(existingBookingEntity));
    }
  }

  @Nested
  class UpdateBooking {

    @Test
    void happy_path() {
      // given
      Booking booking = nextBooking();
      BookingEntity bookingEntity = nextBookingEntity();
      Booking bookingToUpdate =
          nextBooking().toBuilder()
              .id(booking.getId())
              .uuid(booking.getUuid())
              .version(booking.getVersion())
              .createdAt(booking.getCreatedAt())
              .updatedAt(booking.getUpdatedAt())
              .build();

      doReturn(Optional.of(bookingEntity)).when(bookingRepository).findByUuid(any());
      doReturn(bookingEntity).when(bookingMapper).toBookingEntity(any());

      doReturn(singletonList(bookingEntity))
          .when(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(any(), any(), any());
      doReturn(singletonList(booking)).when(bookingMapper).toBookingsList(any());

      doReturn(bookingEntity).when(bookingRepository).saveAndFlush(any());
      doReturn(bookingToUpdate).when(bookingMapper).toBooking(any(BookingEntity.class));
      // when
      Booking result = classUnderTest.updateBooking(bookingToUpdate);
      // then
      assertThat(result).isEqualTo(bookingToUpdate);
      verify(bookingRepository).findByUuid(booking.getUuid());
      verify(bookingMapper).toBookingEntity(bookingToUpdate);
      verify(bookingRepository)
          .findForDateRangeWithPessimisticWriteLocking(
              bookingToUpdate.getStartDate(),
              bookingToUpdate.getEndDate(),
              bookingToUpdate.getCampsiteId());
      verify(bookingMapper).toBookingsList(singletonList(bookingEntity));
      verify(bookingRepository).saveAndFlush(bookingEntity);
      verify(bookingMapper, times(2)).toBooking(bookingEntity);
    }
  }

  @Nested
  class CancelBooking {

    @Test
    void happy_path() {
      // given
      Booking booking = nextBooking();
      BookingEntity bookingEntity = nextBookingEntity();
      doReturn(Optional.of(bookingEntity)).when(bookingRepository).findByUuid(any());
      doReturn(bookingEntity).when(bookingRepository).save(any());
      doReturn(bookingEntity).when(bookingMapper).toBookingEntity(any());
      doReturn(booking).when(bookingMapper).toBooking(any(BookingEntity.class));
      // when
      boolean result = classUnderTest.cancelBooking(booking.getUuid());
      // then
      assertThat(result).isTrue();
      verify(bookingRepository).findByUuid(booking.getUuid());
      verify(bookingRepository).save(bookingEntity);
      verify(bookingMapper).toBookingEntity(booking);
      verify(bookingMapper, times(2)).toBooking(bookingEntity);
    }
  }
}
