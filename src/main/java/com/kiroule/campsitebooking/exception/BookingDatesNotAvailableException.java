package com.kiroule.campsitebooking.exception;

/**
 * @author Igor Baiborodine
 */
public class BookingDatesNotAvailableException extends RuntimeException {

  public BookingDatesNotAvailableException(String message) {
    super(message);
  }
}
