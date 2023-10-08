package com.kiroule.campsite.booking.api;

import static java.time.LocalDate.now;
import static lombok.AccessLevel.PRIVATE;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto.BookingDtoBuilder;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.Booking.BookingBuilder;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity.BookingEntityBuilder;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

@NoArgsConstructor(access = PRIVATE)
@Generated
public class TestDataGenerator {

  private static final EasyRandom EASY_RANDOM =
      new EasyRandom(
          new EasyRandomParameters().stringLengthRange(5, 10).dateRange(now(), now().plusDays(25)));

  public static Long nextCampsiteId() {
    return EASY_RANDOM.nextLong(Short.MAX_VALUE);
  }

  public static BookingDto nextBookingDto() {
    return nextBookingDto(1L);
  }

  public static BookingDto nextBookingDto(long daysToAddEndDate) {
    BookingDtoBuilder builder =
        EASY_RANDOM.nextObject(BookingDto.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(daysToAddEndDate)).build();
  }

  public static Booking nextBooking() {
    return nextBooking(1L);
  }

  public static Booking nextBooking(long daysToAddEndDate) {
    BookingBuilder builder =
        EASY_RANDOM.nextObject(Booking.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .version(0L)
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(daysToAddEndDate)).build();
  }

  public static BookingEntity nextBookingEntity() {
    return nextBookingEntity(1L);
  }

  public static BookingEntity nextBookingEntity(long daysToAddEndDate) {
    BookingEntityBuilder builder =
        EASY_RANDOM.nextObject(BookingEntity.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .version(0L)
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(daysToAddEndDate)).build();
  }
}
