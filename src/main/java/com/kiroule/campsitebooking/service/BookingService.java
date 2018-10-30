package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.exception.IllegalBookingStateException;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {

  List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate);

  Booking findBookingById(long id) throws BookingNotFoundException;

  Booking createBooking(Booking booking)
      throws IllegalBookingStateException, BookingDatesNotAvailableException;

  Booking updateBooking(Booking booking)
      throws IllegalBookingStateException, BookingNotFoundException, BookingDatesNotAvailableException;

  boolean cancelBooking(long id) throws BookingNotFoundException;
}
