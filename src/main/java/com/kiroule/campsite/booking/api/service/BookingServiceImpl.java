package com.kiroule.campsite.booking.api.service;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.exception.IllegalBookingStateException;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link BookingService} interface implementation.
 *
 * @author Igor Baiborodine
 */
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

  private BookingRepository bookingRepository;

  @Override
  @Transactional(readOnly = true)
  public List<LocalDate> findVacantDays(LocalDate startDate, LocalDate endDate, Long campsiteId) {

    var now = LocalDate.now();
    checkArgument(startDate.isAfter(now), "Start date must be in the future");
    checkArgument(endDate.isAfter(now), "End date must be in the future");
    checkArgument(startDate.isEqual(endDate) || startDate.isBefore(endDate),
        "End date must be equal to start date or greater than start date");

    var vacantDays = startDate.datesUntil(endDate.plusDays(1)).collect(toList());
    var bookings = bookingRepository.findForDateRange(startDate, endDate, campsiteId);
    bookings.forEach(b -> vacantDays.removeAll(b.getBookingDatesWithEndDateExclusive()));
    return vacantDays;
  }

  @Override
  @Transactional(readOnly = true)
  public Booking findByUuid(UUID uuid) {

    return bookingRepository.findByUuid(uuid).orElseThrow(
        () -> new BookingNotFoundException(String.format("Booking was not found for uuid=%s", uuid)));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Retryable(
      include = CannotAcquireLockException.class,
      maxAttempts = 2,
      backoff = @Backoff(delay = 500, maxDelay = 1000))
  public Booking createBooking(Booking booking) {

    if (!booking.isNew()) {
      throw new IllegalBookingStateException("New booking must not have persistence id");
    }
    validateVacantDates(booking);
    booking.setActive(true);

    return bookingRepository.save(booking);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Retryable(include = CannotAcquireLockException.class,
      maxAttempts = 5, backoff=@Backoff(delay = 500, maxDelay = 1000))
  public Booking updateBooking(Booking booking) {

    var persistedBooking = findByUuid(booking.getUuid());

    if (!persistedBooking.isActive()) {
      var message = String.format("Booking with uuid=%s is cancelled", booking.getUuid());
      throw new IllegalBookingStateException(message);
    }
    validateVacantDates(booking);

    // cancelBooking method should be used to cancel booking
    booking.setActive(persistedBooking.isActive());
    return bookingRepository.save(booking);
  }

  @Override
  @Transactional
  public boolean cancelBooking(UUID uuid) {

    var booking = findByUuid(uuid);
    booking.setActive(false);
    booking = bookingRepository.save(booking);
    return !booking.isActive();
  }

  private void validateVacantDates(Booking booking) {

    var vacantDays =
        booking.getStartDate().datesUntil(booking.getEndDate().plusDays(1)).collect(toList());
    var bookings = bookingRepository.findForDateRangeWithPessimisticWriteLocking(
        booking.getStartDate(), booking.getEndDate(), booking.getCampsiteId());
    bookings.forEach(b -> {
      if (!b.getUuid().equals(booking.getUuid())) {
        vacantDays.removeAll(b.getBookingDatesWithEndDateExclusive());
      }
    });

    if (!vacantDays.containsAll(booking.getBookingDatesWithEndDateExclusive())) {
      var message = String.format("No vacant dates available from %s to %s",
          booking.getStartDate(), booking.getEndDate());
      throw new BookingDatesNotAvailableException(message);
    }
  }

}
