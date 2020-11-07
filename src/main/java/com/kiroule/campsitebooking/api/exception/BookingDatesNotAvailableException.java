package com.kiroule.campsitebooking.api.exception;

/**
 * @author Igor Baiborodine
 */
public class BookingDatesNotAvailableException extends RuntimeException {

  private static final long serialVersionUID = -8003308087986227477L;

  public BookingDatesNotAvailableException(String message) {
    super(message);
  }
}
