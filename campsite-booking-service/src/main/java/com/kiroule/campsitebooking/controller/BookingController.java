package com.kiroule.campsitebooking.controller;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsitebooking.contract.v2.BookingApiContractV2;
import com.kiroule.campsitebooking.contract.v2.dto.BookingDto;
import com.kiroule.campsitebooking.mapper.BookingMapperImpl;
import com.kiroule.campsitebooking.service.BookingService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Igor Baiborodine
 */
@RestController
@AllArgsConstructor
public class BookingController implements BookingApiContractV2 {

  private BookingService bookingService;

  private BookingMapperImpl bookingMapper;

  public ResponseEntity<List<LocalDate>> getVacantDates(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {
    if (isNull(startDate)) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (isNull(endDate)) {
      endDate = startDate.plusMonths(1);
    }
    var vacantDates = bookingService.findVacantDates(startDate, endDate, campsiteId);
    return new ResponseEntity<>(vacantDates, OK);
  }

  public ResponseEntity<BookingDto> getBooking(UUID uuid) {
    var booking = bookingService.findByUuid(uuid);
    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), OK);
  }

  public ResponseEntity<BookingDto> addBooking(BookingDto bookingDto) {
    var booking = bookingService.createBooking(bookingMapper.toBooking(bookingDto));
    var selfLink = WebMvcLinkBuilder.linkTo(this.getClass()).slash(booking.getUuid()).withSelfRel();
    var headers = new HttpHeaders();
    headers.setLocation(URI.create(selfLink.getHref()));

    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), headers, CREATED);
  }

  public ResponseEntity<BookingDto> updateBooking(UUID uuid, BookingDto bookingDto) {
    var booking = bookingService.updateBooking(bookingMapper.toBooking(bookingDto));
    return new ResponseEntity<>(bookingMapper.toBookingDto(booking), OK);
  }

  public ResponseEntity<Void> cancelBooking(UUID uuid) {
    var cancelled = bookingService.cancelBooking(uuid);
    if (cancelled) {
      return new ResponseEntity<>(OK);
    }
    return new ResponseEntity<>(BAD_REQUEST);
  }
}
