package com.kiroule.campsite.booking.api.exception;

/**
 * @author Igor Baiborodine
 */
public class BookingDatesNotAvailableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BookingDatesNotAvailableException(String message) {
    super(message);
  }
}
