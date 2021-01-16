package com.kiroule.campsite.booking.api.controller;

import com.kiroule.campsite.booking.api.contract.v1.BookingApiContractV1;
import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.mapper.BookingMapper;
import com.kiroule.campsite.booking.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @author Igor Baiborodine
 */
@RestController
public class BookingController implements BookingApiContractV1 {

  private BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  public ResponseEntity<List<LocalDate>> getVacantDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (endDate == null) {
      endDate = startDate.plusMonths(1);
    }
    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
    return new ResponseEntity<>(vacantDates, HttpStatus.OK);
  }

  public ResponseEntity<EntityModel<BookingDto>> getBooking(UUID uuid) {
    Booking booking = bookingService.findBookingByUuid(uuid);
    return new ResponseEntity<>(getResource(booking), HttpStatus.OK);
  }

  public ResponseEntity<EntityModel<BookingDto>> addBooking(BookingDto bookingDto) {
    Booking addedBooking = bookingService.createBooking(BookingMapper.INSTANCE.toBooking(bookingDto));
    EntityModel<BookingDto> resource = getResource(addedBooking);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(resource.getRequiredLink(IanaLinkRelations.SELF).getHref()));

    return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
  }

  public ResponseEntity<EntityModel<BookingDto>> updateBooking(UUID uuid, BookingDto bookingDto) {
    Booking updatedBooking = bookingService.updateBooking(
        BookingMapper.INSTANCE.toBooking(bookingDto));
    return new ResponseEntity<>(getResource(updatedBooking), HttpStatus.OK);
  }

  public ResponseEntity<Void> cancelBooking(UUID uuid) {
    boolean cancelled = bookingService.cancelBooking(uuid);
    if (cancelled) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  private EntityModel<BookingDto> getResource(Booking booking) {
    EntityModel<BookingDto> resource = EntityModel.of(BookingMapper.INSTANCE.toBookingDto(booking));
    Link selfLink = WebMvcLinkBuilder
        .linkTo(this.getClass()).slash(booking.getUuid()).withSelfRel();
    resource.add(selfLink);
    return resource;
  }

}