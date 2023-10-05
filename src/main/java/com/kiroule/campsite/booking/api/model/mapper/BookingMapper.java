package com.kiroule.campsite.booking.api.model.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import com.kiroule.campsite.booking.api.contract.v2.model.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.service.CampsiteService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = CONSTRUCTOR,
    uses = {CampsiteService.class})
public interface BookingMapper {

  @Mapping(source = "campsiteId", target = "campsite")
  Booking toBooking(BookingDto bookingDto);

  @Mapping(source = "campsite.id", target = "campsiteId")
  BookingDto toBookingDto(Booking booking);
}
