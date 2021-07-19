package com.kiroule.campsite.booking.api.exception;

/**
 * @author Igor Baiborodine
 */
public class BookingNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BookingNotFoundException(String message) {
    super(message);
  }
}
