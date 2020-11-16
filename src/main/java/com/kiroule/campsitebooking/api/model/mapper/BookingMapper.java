package com.kiroule.campsitebooking.api.model.mapper;

import com.kiroule.campsitebooking.api.model.Booking;
import com.kiroule.campsitebooking.api.model.dto.BookingDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    BookingDto toBookingDto(Booking booking);

    Booking toBooking(BookingDto bookingDto);

}
