package com.kiroule.campsitebooking;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class AbstractTest {

  protected Booking createBooking(LocalDate startDate, LocalDate endDate) {
    return createBooking("John Smith", "john.smith@domain.com", startDate, endDate);
  }

  protected Booking createBooking(
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
