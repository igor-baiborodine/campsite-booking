package com.kiroule.campsitebooking.api.service;

import com.kiroule.campsitebooking.api.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {

  List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate);

  Booking findBookingByUuid(UUID uuid);

  Booking createBooking(Booking booking);

  Booking updateBooking(Booking booking);

  boolean cancelBooking(UUID uuid);
}
