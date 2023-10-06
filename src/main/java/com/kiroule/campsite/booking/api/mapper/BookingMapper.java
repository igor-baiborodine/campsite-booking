package com.kiroule.campsite.booking.api.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.service.CampsiteService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR,
    uses = {CampsiteService.class})
public interface BookingMapper {

  @Mapping(source = "campsite.id", target = "campsiteId")
  BookingDto toBookingDto(Booking booking);

  BookingEntity toBookingEntity(Booking booking);

  @Mapping(source = "campsiteId", target = "campsite")
  Booking toBooking(BookingDto bookingDto);

  Booking toBooking(BookingEntity bookingEntity);
}
