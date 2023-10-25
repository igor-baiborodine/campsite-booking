package com.kiroule.campsite.booking.api.exception;

import java.io.Serial;

/**
 * @author Igor Baiborodine
 */
public class BookingDatesNotAvailableException extends RuntimeException {

  @Serial private static final long serialVersionUID = 3055008776888916764L;

  public BookingDatesNotAvailableException(String message) {
    super(message);
  }
}
