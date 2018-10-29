package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link BookingService} interface implementation.
 *
 * @author Igor Baiborodine
 */
@Service
public class BookingServiceImpl implements BookingService {

  private BookingRepository bookingRepository;

  @Autowired
  public BookingServiceImpl(BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<LocalDate> checkBookingAvailability(LocalDate startDate, LocalDate endDate) {
    List<LocalDate> result = startDate
        .datesUntil(endDate.plusDays(1))
        .collect(Collectors.toList());
    List<Booking> bookings = bookingRepository.findForDateRange(startDate, endDate);

    bookings.forEach(b -> {
      List<LocalDate> bookedDates = b.getStartDate()
          .datesUntil(b.getEndDate())
          .collect(Collectors.toList());
      result.removeAll(bookedDates);
    });
    return result;
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
  @Transactional
  public Booking saveBooking(Booking booking) {
    // TODO: add booking dates availability validation
    return bookingRepository.save(booking);
  }

  @Override
  @Transactional
  public boolean cancelBooking(long id) throws BookingNotFoundException {
    Booking booking = findBookingById(id);
    booking.setActive(false);
    booking = bookingRepository.save(booking);
    return !booking.isActive();
  }

}
