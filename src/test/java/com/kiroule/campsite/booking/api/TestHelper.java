package com.kiroule.campsite.booking.api;

import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.mapper.BookingMapper;
import java.time.LocalDate;
import java.util.UUID;

public class TestHelper {

  public static final String FULL_NAME = "John Smith";
  public static final String EMAIL = "john.smith@domain.com";

  public static BookingDto buildBookingDto(LocalDate startDate, LocalDate endDate) {
    return buildBookingDto(startDate, endDate, UUID.randomUUID(), FULL_NAME, EMAIL);
  }
  public static Booking buildBooking(LocalDate startDate, LocalDate endDate) {
    return BookingMapper.INSTANCE.toBooking(buildBookingDto(startDate, endDate));
  }

  public static BookingDto buildBookingDto(LocalDate startDate, LocalDate endDate, UUID uuid) {
    return buildBookingDto(startDate, endDate, uuid, FULL_NAME, EMAIL);
  }

  public static Booking buildBooking(LocalDate startDate, LocalDate endDate, UUID uuid) {
    return BookingMapper.INSTANCE.toBooking(
        buildBookingDto(startDate, endDate, uuid, FULL_NAME, EMAIL));
  }

  public static BookingDto buildBookingDto(
      LocalDate startDate, LocalDate endDate, UUID uuid, String fullName, String email) {
    return BookingDto.builder()
        .startDate(startDate)
        .endDate(endDate)
        .uuid(uuid)
        .fullName(fullName)
        .email(email)
        .active(true)
        .build();
  }
}
