package com.kiroule.campsitebooking.api;

import com.kiroule.campsitebooking.api.contract.v1.model.BookingDto;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

  public BookingDto buildBooking(LocalDate startDate, LocalDate endDate) {
    return buildBooking(UUID.randomUUID(), "John Smith", "john.smith@domain.com", startDate, endDate);
  }

  public BookingDto buildBooking(UUID uuid, LocalDate startDate, LocalDate endDate) {
    return buildBooking(uuid, "John Smith", "john.smith@domain.com", startDate, endDate);
  }

  public BookingDto buildBooking(
      UUID uuid, String fullName, String email, LocalDate startDate, LocalDate endDate) {
    return BookingDto.builder()
        .uuid(uuid)
        .fullName(fullName)
        .email(email)
        .startDate(startDate)
        .endDate(endDate)
        .active(true)
        .build();
  }
}
