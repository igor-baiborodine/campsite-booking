package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link BookingService} interface implementation.
 *
 * @author Igor Baiborodine
 */
public class BookingServiceImpl implements BookingService {

  private BookingRepository bookingRepository;

  @Autowired
  public BookingServiceImpl(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Override
  public Collection<LocalDate> checkBookingAvailability(LocalDate startDate, LocalDate endDate) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public Booking findBookingById(long id) throws BookingNotFoundException {
    Optional<Booking> booking = bookingRepository.findById(id);
    if (!booking.isPresent()) {
      throw new BookingNotFoundException(String.format("Cannot find booking for ID [%d]", id));
    }
    return booking.get();
  }

  @Override
  public Booking saveBooking(Booking booking) {
    return null;
  }

  @Override
  public Booking cancelBooking(long id) {
    return null;
  }
}
