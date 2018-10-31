package com.kiroule.campsitebooking.api.rest;

import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Resource<Booking> getBooking(@PathVariable("id") long id)
      throws BookingNotFoundException {

    Booking booking = bookingService.findBookingById(id);
    Resource<Booking> resource = new Resource<>(booking);
    Link selfLink = ControllerLinkBuilder.linkTo(
        ControllerLinkBuilder.methodOn(this.getClass()).getBooking(id)).withSelfRel();
    resource.add(selfLink);

    return resource;
  }

}
