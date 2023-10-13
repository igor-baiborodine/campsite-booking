package com.kiroule.campsite.booking.api.model.mapper;

import static com.kiroule.campsite.booking.api.TestDataHelper.*;
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
      BookingDto bookingDto = nextBookingDto();
      // when
      Booking result = classUnderTest.toBooking(bookingDto);
      // then
      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields("id", "version", "createdAt", "updatedAt")
          .isEqualTo(bookingDto);
    }

    @Test
    void given_null_booking_dto__then_null_returned() {
      // given
      BookingDto bookingDto = null;
      // when
      Booking result = classUnderTest.toBooking(bookingDto);
      // then
      assertThat(result).isNull();
    }
  }

  @Nested
  class ToBookingFromEntity {

    @Test
    void happy_path() {
      // given
      BookingEntity bookingEntity = nextBookingEntity();
      // when
      Booking result = classUnderTest.toBooking(bookingEntity);
      // then
      assertThat(result).usingRecursiveComparison().isEqualTo(bookingEntity);
    }

    @Test
    void given_null_booking_entity__then_null_returned() {
      // given
      BookingEntity bookingEntity = null;
      // when
      Booking result = classUnderTest.toBooking(bookingEntity);
      // then
      assertThat(result).isNull();
    }
  }

  @Nested
  class ToBookingDto {

    @Test
    void happy_path() {
      // given
      Booking booking = nextBooking();
      // when
      BookingDto result = classUnderTest.toBookingDto(booking);
      // then
      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields("id", "version")
          .isEqualTo(booking);
    }

    @Test
    void given_null_booking__then_null_returned() {
      // given
      Booking booking = null;
      // when
      BookingDto result = classUnderTest.toBookingDto(booking);
      // then
      assertThat(result).isNull();
    }
  }

  @Nested
  class ToBookingEntity {

    @Test
    void happy_path() {
      // given
      Booking booking = nextBooking();
      // when
      BookingEntity result = classUnderTest.toBookingEntity(booking);
      // then
      assertThat(result).usingRecursiveComparison().isEqualTo(booking);
    }

    @Test
    void given_null_booking__then_null_returned() {
      // given
      Booking booking = null;
      // when
      BookingEntity result = classUnderTest.toBookingEntity(booking);
      // then
      assertThat(result).isNull();
    }
  }

  @Nested
  class ToBookingsList {

    @Test
    void happy_path() {
      // given
      List<BookingEntity> bookingEntities = asList(nextBookingEntity(), nextBookingEntity());
      // when
      List<Booking> result = classUnderTest.toBookingsList(bookingEntities);
      // then
      for (int i = 0; i < result.size(); i++) {
        assertThat(result.get(i)).usingRecursiveComparison().isEqualTo(bookingEntities.get(i));
      }
    }
  }
}
