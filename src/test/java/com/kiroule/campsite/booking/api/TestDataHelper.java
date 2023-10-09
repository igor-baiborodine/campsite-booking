package com.kiroule.campsite.booking.api;

import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto.BookingDtoBuilder;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.Booking.BookingBuilder;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity.BookingEntityBuilder;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Generated
public class TestDataHelper {

  private static final EasyRandom EASY_RANDOM =
      new EasyRandom(
          new EasyRandomParameters()
              .stringLengthRange(5, 10)
              .dateRange(now().plusDays(1), now().plusDays(25)));

  @Autowired CampsiteRepository campsiteRepository;

  @Autowired BookingRepository bookingRepository;

  private static Long nextCampsiteId() {
    return EASY_RANDOM.nextLong(Short.MAX_VALUE);
  }

  private static Integer nextCampsiteCapacity() {
    return EASY_RANDOM.nextInt(Byte.MAX_VALUE);
  }

  public static Campsite nextCampsite() {
    return EASY_RANDOM.nextObject(Campsite.class).toBuilder()
        .capacity(nextCampsiteCapacity())
        .active(true)
        .build();
  }

  public static CampsiteEntity nextCampsiteEntity() {
    return EASY_RANDOM.nextObject(CampsiteEntity.class).toBuilder()
        .capacity(nextCampsiteCapacity())
        .active(true)
        .build();
  }

  public static BookingDto nextBookingDto() {
    BookingDtoBuilder builder =
        EASY_RANDOM.nextObject(BookingDto.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(1L)).build();
  }

  public static Booking nextBooking() {
    BookingBuilder<?, ?> builder =
        EASY_RANDOM.nextObject(Booking.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .version(0L)
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(1L)).build();
  }

  public static BookingEntity nextBookingEntity() {
    BookingEntityBuilder<?, ?> builder =
        EASY_RANDOM.nextObject(BookingEntity.class).toBuilder()
            .campsiteId(nextCampsiteId())
            .version(0L)
            .active(true);
    return builder.endDate(builder.build().getStartDate().plusDays(1L)).build();
  }

  public CampsiteEntity createCampsiteEntity() {
    CampsiteEntity campsiteEntity =
        nextCampsiteEntity().toBuilder().id(null).createdAt(null).updatedAt(null).build();

    CampsiteEntity savedCampsiteEntity = campsiteRepository.saveAndFlush(campsiteEntity);
    assumeTrue(nonNull(savedCampsiteEntity.getId()));

    return savedCampsiteEntity;
  }

  public BookingEntity createBookingEntity(Long campsiteId) {
    return createBookingEntity(campsiteId, null, null);
  }

  public BookingEntity createBookingEntity(
      Long campsiteId, Integer startDateDaysToAdd, Integer endDateDaysToAdd) {
    BookingEntityBuilder<?, ?> builder =
        nextBookingEntity().toBuilder()
            .id(null)
            .version(null)
            .campsiteId(campsiteId)
            .createdAt(null)
            .updatedAt(null);

    if (nonNull(startDateDaysToAdd)) {
      builder.startDate(builder.build().getStartDate().plusDays(startDateDaysToAdd));
    }
    if (nonNull(endDateDaysToAdd)) {
      builder.endDate(builder.build().getStartDate().plusDays(endDateDaysToAdd));
    }
    BookingEntity savedBookingEntity = bookingRepository.saveAndFlush(builder.build());
    assumeTrue(nonNull(savedBookingEntity.getId()));
    assumeTrue(savedBookingEntity.getVersion().equals(0L));

    return savedBookingEntity;
  }
}
