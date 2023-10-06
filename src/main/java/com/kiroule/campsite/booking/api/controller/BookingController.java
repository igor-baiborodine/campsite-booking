package com.kiroule.campsite.booking.api.controller;

import static java.util.Objects.isNull;
import static org.springframework.hateoas.IanaLinkRelations.*;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsite.booking.api.contract.v2.BookingApiContractV2;
import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.service.BookingService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
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

  private BookingMapper bookingMapper;

  public ResponseEntity<List<LocalDate>> getVacantDates(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {
    if (isNull(startDate)) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (isNull(endDate)) {
      endDate = startDate.plusMonths(1);
    }
    var vacantDates = bookingService.findVacantDays(startDate, endDate, campsiteId);
    return new ResponseEntity<>(vacantDates, OK);
  }

  public ResponseEntity<EntityModel<BookingDto>> getBooking(UUID uuid) {
    var booking = bookingService.findByUuid(uuid);
    return new ResponseEntity<>(getResource(booking), OK);
  }

  public ResponseEntity<EntityModel<BookingDto>> addBooking(BookingDto bookingDto) {
    var addedBooking = bookingService.createBooking(bookingMapper.toBooking(bookingDto));
    var resource = getResource(addedBooking);
    var headers = new HttpHeaders();
    headers.setLocation(URI.create(resource.getRequiredLink(SELF).getHref()));

    return new ResponseEntity<>(resource, headers, CREATED);
  }

  public ResponseEntity<EntityModel<BookingDto>> updateBooking(UUID uuid, BookingDto bookingDto) {
    var updatedBooking = bookingService.updateBooking(bookingMapper.toBooking(bookingDto));
    return new ResponseEntity<>(getResource(updatedBooking), OK);
  }

  public ResponseEntity<Void> cancelBooking(UUID uuid) {
    var cancelled = bookingService.cancelBooking(uuid);
    if (cancelled) {
      return new ResponseEntity<>(OK);
    }
    return new ResponseEntity<>(BAD_REQUEST);
  }

  private EntityModel<BookingDto> getResource(Booking booking) {
    var resource = EntityModel.of(bookingMapper.toBookingDto(booking));
    var selfLink = WebMvcLinkBuilder.linkTo(this.getClass()).slash(booking.getUuid()).withSelfRel();
    resource.add(selfLink);
    return resource;
  }
}
