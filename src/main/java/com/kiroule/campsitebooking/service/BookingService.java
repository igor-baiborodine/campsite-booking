package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {

  List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate);

  Booking findBookingById(long id);

  Booking createBooking(Booking booking);

  Booking updateBooking(Long id, Booking booking);

  boolean cancelBooking(long id);
}
