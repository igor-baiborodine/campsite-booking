package com.kiroule.campsitebooking.exception;

/**
 * @author Igor Baiborodine
 */
public class BookingNotFoundException extends RuntimeException {

  public BookingNotFoundException(String message) {
    super(message);
  }
}
