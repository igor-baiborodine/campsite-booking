package com.kiroule.campsitebooking.api.rest;

import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.service.BookingService;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Igor Baiborodine
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

  private BookingService bookingService;

  @Autowired
  public BookingRestController(BookingService bookingService) {
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

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<Booking>> getBooking(@PathVariable() long id) {

    Booking booking = bookingService.findBookingById(id);
    Resource<Booking> resource = new Resource<>(booking);

    Link selfLink = ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(this.getClass()).getBooking(id)).withSelfRel();
    resource.add(selfLink);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<Booking>> addBooking(@RequestBody() @Valid Booking booking) {

    Booking addedBooking = bookingService.createBooking(booking);
    Resource<Booking> resource = new Resource<>(addedBooking);

    Link selfLink = ControllerLinkBuilder
        .linkTo(this.getClass()).slash(addedBooking.getId()).withSelfRel();
    resource.add(selfLink);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(selfLink.getHref()));
    return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Resource<Booking>> updateBooking(
      @PathVariable("id") long id, @RequestBody @Valid Booking booking) {

    Booking updatedBooking = bookingService.updateBooking(id, booking);
    Resource<Booking> resource = new Resource<>(updatedBooking);

    Link selfLink = ControllerLinkBuilder
        .linkTo(this.getClass()).slash(updatedBooking.getId()).withSelfRel();
    resource.add(selfLink);
    return new ResponseEntity<>(resource, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Void> updateBooking(@PathVariable("id") long id) {

    boolean cancelled = bookingService.cancelBooking(id);
    if (cancelled) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

}
