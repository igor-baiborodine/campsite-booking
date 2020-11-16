package com.kiroule.campsitebooking.api.controller;

import com.kiroule.campsitebooking.api.model.Booking;
import com.kiroule.campsitebooking.api.model.dto.BookingDto;
import com.kiroule.campsitebooking.api.model.mapper.BookingMapper;
import com.kiroule.campsitebooking.api.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @author Igor Baiborodine
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private BookingService bookingService;

  @Autowired
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping(value = "/vacant-dates", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<LocalDate>> getVacantDates(
      @RequestParam(name = "start_date", required = false)
      @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam(name = "end_date", required = false)
      @DateTimeFormat(iso = ISO.DATE) LocalDate endDate) {

    if (startDate == null) {
      startDate = LocalDate.now().plusDays(1);
    }
    if (endDate == null) {
      endDate = startDate.plusMonths(1);
    }
    List<LocalDate> vacantDates = bookingService.findVacantDays(startDate, endDate);
    return new ResponseEntity<>(vacantDates, HttpStatus.OK);
  }

  @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<BookingDto>> getBooking(@PathVariable() UUID uuid) {

    Booking booking = bookingService.findBookingByUuid(uuid);
    return new ResponseEntity<>(getResource(booking), HttpStatus.OK);
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<BookingDto>> addBooking(@RequestBody() @Valid BookingDto bookingDto) {

    Booking addedBooking = bookingService.createBooking(BookingMapper.INSTANCE.toBooking(bookingDto));
    Resource<BookingDto> resource = getResource(addedBooking);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(resource.getLinks().get(0).getHref()));

    return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<BookingDto>> updateBooking(
      @PathVariable("uuid") UUID uuid, @RequestBody @Valid BookingDto bookingDto) {

    Booking updatedBooking = bookingService.updateBooking(
        BookingMapper.INSTANCE.toBooking(bookingDto));
    return new ResponseEntity<>(getResource(updatedBooking), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Void> updateBooking(@PathVariable("uuid") UUID uuid) {

    boolean cancelled = bookingService.cancelBooking(uuid);
    if (cancelled) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  private Resource<BookingDto> getResource(Booking booking) {
    Resource<BookingDto> resource = new Resource<>(BookingMapper.INSTANCE.toBookingDto(booking));

    Link selfLink = ControllerLinkBuilder
        .linkTo(this.getClass()).slash(booking.getUuid()).withSelfRel();
    resource.add(selfLink);
    return resource;
  }

}
