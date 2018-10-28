package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.Collection;

public interface BookingService {

  Collection<LocalDate> checkBookingAvailability(LocalDate startDate, LocalDate endDate);

  Booking findBookingById(long id) throws BookingNotFoundException;

  Booking saveBooking(Booking booking);

  boolean cancelBooking(long id) throws BookingNotFoundException;
}
