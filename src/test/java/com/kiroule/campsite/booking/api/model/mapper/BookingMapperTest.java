package com.kiroule.campsite.booking.api.model.mapper;

import static com.kiroule.campsite.booking.api.TestDataGenerator.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.mapper.BookingMapperImpl;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BookingMapperTest {

  BookingMapperImpl classUnderTest = new BookingMapperImpl();

  @Nested
  class ToBookingFromDto {

    @Test
    void happy_path() {
      // given
      var bookingDto = nextBookingDto();
      // when
      var booking = classUnderTest.toBooking(bookingDto);
      // then
      assertThat(booking)
          .usingRecursiveComparison()
          .ignoringFields("id", "version", "createdAt", "updatedAt")
          .isEqualTo(bookingDto);
    }

    @Test
    void given_null_booking_dto__then_null_returned() {
      // given
      BookingDto bookingDto = null;
      // when
      var booking = classUnderTest.toBooking(bookingDto);
      // then
      assertThat(booking).isNull();
    }
  }

  @Nested
  class ToBookingFromEntity {

    @Test
    void happy_path() {
      // given
      var bookingEntity = nextBookingEntity();
      // when
      var booking = classUnderTest.toBooking(bookingEntity);
      // then
      assertThat(booking).usingRecursiveComparison().isEqualTo(bookingEntity);
    }

    @Test
    void given_null_booking_entity__then_null_returned() {
      // given
      BookingEntity bookingEntity = null;
      // when
      var booking = classUnderTest.toBooking(bookingEntity);
      // then
      assertThat(booking).isNull();
    }
  }

  @Nested
  class ToBookingDto {

    @Test
    void happy_path() {
      // given
      var booking = nextBooking();
      // when
      var bookingDto = classUnderTest.toBookingDto(booking);
      // then
      assertThat(bookingDto)
          .usingRecursiveComparison()
          .ignoringFields("id", "version")
          .isEqualTo(booking);
    }

    @Test
    void given_null_booking__then_null_returned() {
      // given
      Booking booking = null;
      // when
      var bookingDto = classUnderTest.toBookingDto(booking);
      // then
      assertThat(bookingDto).isNull();
    }
  }

  @Nested
  class ToBookingEntity {

    @Test
    void happy_path() {
      // given
      var booking = nextBooking();
      // when
      var bookingEntity = classUnderTest.toBookingEntity(booking);
      // then
      assertThat(bookingEntity).usingRecursiveComparison().isEqualTo(booking);
    }

    @Test
    void given_null_booking__then_null_returned() {
      // given
      Booking booking = null;
      // when
      var bookingEntity = classUnderTest.toBookingEntity(booking);
      // then
      assertThat(bookingEntity).isNull();
    }
  }

  @Nested
  class ToBookingsList {

    @Test
    void happy_path() {
      // given
      List<BookingEntity> bookingEntities = asList(nextBookingEntity(), nextBookingEntity());
      // when
      List<Booking> bookings = classUnderTest.toBookingsList(bookingEntities);
      // then
      for (int i = 0; i < bookings.size(); i++) {
        assertThat(bookings.get(i)).usingRecursiveComparison().isEqualTo(bookingEntities.get(i));
      }
    }
  }
}
