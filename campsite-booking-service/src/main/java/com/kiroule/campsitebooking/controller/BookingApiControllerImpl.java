package com.kiroule.campsitebooking.controller;

import static java.util.Objects.isNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsitebooking.api.v2.BookingApiDelegate;
import com.kiroule.campsitebooking.api.v2.dto.BookingDto;
import com.kiroule.campsitebooking.mapper.BookingMapper;
import com.kiroule.campsitebooking.service.BookingService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Igor Baiborodine
 */
@Service
@AllArgsConstructor
public class BookingApiControllerImpl implements BookingApiDelegate {

  private BookingService bookingService;

  private BookingMapper bookingMapper;

  @Override
  public ResponseEntity<List<LocalDate>> getVacantDates(
      Long campsiteId, LocalDate startDate, LocalDate endDate) {
    if (isNull(startDate)) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (isNull(endDate)) {
      endDate = startDate.plusMonths(1);
    }
    var vacantDates = bookingService.findVacantDates(startDate, endDate, campsiteId);
    return new ResponseEntity<>(vacantDates, OK);
  }

  @Override
  public ResponseEntity<BookingDto> getBooking(UUID uuid) {
    var booking = bookingService.findByUuid(uuid);
    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), OK);
  }

  @Override
  public ResponseEntity<BookingDto> addBooking(BookingDto bookingDto) {
    var booking = bookingService.createBooking(bookingMapper.toBooking(bookingDto));
    var headers = new HttpHeaders();
    headers.setLocation(
        linkTo(BookingApiControllerImpl.class)
            .slash("api/v2/booking")
            .slash(booking.getUuid())
            .toUri());
    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), headers, CREATED);
  }

  @Override
  public ResponseEntity<BookingDto> updateBooking(UUID uuid, BookingDto bookingDto) {
    var booking = bookingService.updateBooking(bookingMapper.toBooking(bookingDto));
    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), OK);
  }

  @Override
  public ResponseEntity<Void> cancelBooking(UUID uuid) {
    var cancelled = bookingService.cancelBooking(uuid);
    if (cancelled) {
      return new ResponseEntity<>(OK);
    }
    return new ResponseEntity<>(BAD_REQUEST);
  }
}
