package com.kiroule.campsite.booking.api.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface BookingMapper {

  BookingDto toBookingDto(Booking booking);

  BookingEntity toBookingEntity(Booking booking);

  Booking toBooking(BookingDto bookingDto);

  Booking toBooking(BookingEntity bookingEntity);

  default List<Booking> toBookingsList(List<BookingEntity> bookingEntities) {
    return bookingEntities.stream().map(this::toBooking).toList();
  }
}
