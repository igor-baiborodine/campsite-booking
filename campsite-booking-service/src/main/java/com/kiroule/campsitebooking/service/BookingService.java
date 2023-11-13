package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.model.Booking;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
public interface BookingService {

  List<LocalDate> findVacantDates(LocalDate startDate, LocalDate endDate, Long campsiteId);

  Booking findByUuid(UUID uuid);

  Booking createBooking(@Valid Booking booking);

  Booking updateBooking(@Valid Booking booking);

  boolean cancelBooking(UUID uuid);
}
