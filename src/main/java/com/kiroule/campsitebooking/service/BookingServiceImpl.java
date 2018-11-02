package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.exception.IllegalBookingStateException;
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
  public List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate) {

    List<LocalDate> vacantDays = startDate
        .datesUntil(endDate.plusDays(1))
        .collect(Collectors.toList());
    List<Booking> bookings = bookingRepository.findForDateRange(startDate, endDate);

    bookings.forEach(b -> vacantDays.removeAll(b.getBookingDates()));
    return vacantDays;
  }

  @Override
  @Transactional(readOnly = true)
  public Booking findBookingById(long id) {

    Optional<Booking> booking = bookingRepository.findById(id);
    if (!booking.isPresent()) {
      throw new BookingNotFoundException(String.format("Booking was not found for id=%d", id));
    }
    return booking.get();
  }

  @Override
  @Transactional
  public Booking createBooking(Booking booking) {

    if (!booking.isNew()) {
      throw new IllegalBookingStateException("New booking must not have id");
    }
    List<LocalDate> vacantDays = findVacantDays(booking.getStartDate(), booking.getEndDate());

    if (!vacantDays.containsAll(booking.getBookingDates())) {
      String message = String.format("No vacant dates available from %s to %s",
          booking.getStartDate(), booking.getEndDate());
      throw new BookingDatesNotAvailableException(message);
    }
    booking.setActive(true);
    return bookingRepository.save(booking);
  }

  @Override
  @Transactional
  public Booking updateBooking(Long id, Booking booking) {

    Booking persistedBooking = findBookingById(booking.getId());

    if (!persistedBooking.isActive()) {
      String message = String.format("Booking with id=%d is cancelled", booking.getId());
      throw new IllegalBookingStateException(message);
    }
    List<LocalDate> vacantDays = findVacantDays(booking.getStartDate(), booking.getEndDate());
    vacantDays.addAll(persistedBooking.getBookingDates());

    if (!vacantDays.containsAll(booking.getBookingDates())) {
      String message = String.format("No vacant dates available from %s to %s",
          booking.getStartDate(), booking.getEndDate());
      throw new BookingDatesNotAvailableException(message);
    }
    booking.setId(id);
    booking.setActive(persistedBooking.isActive());
    return bookingRepository.save(booking);
  }

  @Override
  @Transactional
  public boolean cancelBooking(long id) {

    Booking booking = findBookingById(id);
    booking.setActive(false);
    booking = bookingRepository.save(booking);
    return !booking.isActive();
  }

}
