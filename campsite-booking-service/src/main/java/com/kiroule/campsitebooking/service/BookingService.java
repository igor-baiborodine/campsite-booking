package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {

  List<LocalDate> findVacantDates(LocalDate startDate, LocalDate endDate, Long campsiteId);

  Booking findByUuid(UUID uuid);

  Booking createBooking(Booking booking);

  Booking updateBooking(Booking booking);

  boolean cancelBooking(UUID uuid);
}
