package com.kiroule.campsitebooking.api;

import com.kiroule.campsitebooking.api.model.Booking;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class TestHelper {

  public Booking buildBooking(LocalDate startDate, LocalDate endDate) {
    return buildBooking("John Smith", "john.smith@domain.com", startDate, endDate);
  }

  public Booking buildBooking(
      String fullName, String email, LocalDate startDate, LocalDate endDate) {
    return Booking.builder()
        .fullName(fullName)
        .email(email)
        .startDate(startDate)
        .endDate(endDate)
        .active(true)
        .build();
  }
}
