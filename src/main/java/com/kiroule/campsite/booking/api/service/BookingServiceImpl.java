package com.kiroule.campsite.booking.api.service;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link BookingService} interface implementation.
 *
 * @author Igor Baiborodine
 */
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

  private BookingRepository bookingRepository;

  private BookingMapper bookingMapper;

  @Override
  @Transactional(readOnly = true)
  public List<LocalDate> findVacantDates(LocalDate startDate, LocalDate endDate, Long campsiteId) {

    var now = LocalDate.now();
    checkArgument(startDate.isAfter(now), "Start date must be in the future");
    checkArgument(endDate.isAfter(now), "End date must be in the future");
    checkArgument(
        startDate.isEqual(endDate) || startDate.isBefore(endDate),
        "End date must be equal to start date or greater than start date");

    var vacantDates = startDate.datesUntil(endDate.plusDays(1)).collect(toList());
    var bookingEntities = bookingRepository.findForDateRange(startDate, endDate, campsiteId);
    bookingMapper
        .toBookingsList(bookingEntities)
        .forEach(b -> vacantDates.removeAll(b.getBookingDatesWithEndDateExclusive()));

    return vacantDates;
  }

  @Override
  @Transactional(readOnly = true)
  public Booking findByUuid(UUID uuid) {

    Supplier<BookingNotFoundException> exceptionSupplier =
        () -> new BookingNotFoundException(format("Booking was not found for uuid=%s", uuid));
    var bookingEntity = bookingRepository.findByUuid(uuid).orElseThrow(exceptionSupplier);

    return bookingMapper.toBooking(bookingEntity);
  }

  @Override
  @Transactional(propagation = REQUIRES_NEW)
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 2,
      backoff = @Backoff(delay = 500, maxDelay = 1000))
  public Booking createBooking(Booking booking) {

    checkArgument(isNull(booking.getUuid()), "New booking must not have UUID");
    checkArgument(booking.isActive(), "Booking must be active");
    validateVacantDates(booking);
    var bookingEntity = bookingMapper.toBookingEntity(booking);

    return bookingMapper.toBooking(bookingRepository.saveAndFlush(bookingEntity));
  }

  @Override
  @Transactional(propagation = REQUIRES_NEW)
  @Retryable(
      retryFor = CannotAcquireLockException.class,
      maxAttempts = 5,
      backoff = @Backoff(delay = 500, maxDelay = 1000))
  public Booking updateBooking(Booking booking) {

    // cancelBooking method should be used to cancel booking
    checkArgument(booking.isActive(), "Booking must be active");
    var persistedBooking = findByUuid(booking.getUuid());
    validateVacantDates(booking);

    booking.setId(persistedBooking.getId());
    booking.setVersion(persistedBooking.getVersion());
    var bookingEntity = bookingMapper.toBookingEntity(booking);
    var savedBookingEntity = bookingRepository.saveAndFlush(bookingEntity);

    return bookingMapper.toBooking(savedBookingEntity);
  }

  @Override
  @Transactional
  public boolean cancelBooking(UUID uuid) {

    var booking = findByUuid(uuid);
    booking.setActive(false);
    var bookingEntity = bookingMapper.toBookingEntity(booking);
    booking = bookingMapper.toBooking(bookingRepository.save(bookingEntity));

    return !booking.isActive();
  }

  private void validateVacantDates(Booking booking) {

    var vacantDays =
        booking.getStartDate().datesUntil(booking.getEndDate().plusDays(1)).collect(toList());
    List<BookingEntity> bookingEntities =
        bookingRepository.findForDateRangeWithPessimisticWriteLocking(
            booking.getStartDate(), booking.getEndDate(), booking.getCampsiteId());
    bookingMapper
        .toBookingsList(bookingEntities)
        .forEach(
            b -> {
              if (!b.getUuid().equals(booking.getUuid())) {
                vacantDays.removeAll(b.getBookingDatesWithEndDateExclusive());
              }
            });

    if (!vacantDays.containsAll(booking.getBookingDatesWithEndDateExclusive())) {
      var message =
          format(
              "No vacant dates available from %s to %s",
              booking.getStartDate(), booking.getEndDate());
      throw new BookingDatesNotAvailableException(message);
    }
  }
}
