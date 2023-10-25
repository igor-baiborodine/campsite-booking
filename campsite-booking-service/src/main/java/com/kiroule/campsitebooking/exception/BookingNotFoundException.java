package com.kiroule.campsitebooking.exception;

import java.io.Serial;

/**
 * @author Igor Baiborodine
 */
public class BookingNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 2394711262440589805L;

  public BookingNotFoundException(String message) {
    super(message);
  }
}
