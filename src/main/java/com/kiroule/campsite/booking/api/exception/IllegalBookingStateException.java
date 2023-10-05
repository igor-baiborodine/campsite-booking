package com.kiroule.campsite.booking.api.exception;

import java.io.Serial;

/**
 * @author Igor Baiborodine
 */
public class IllegalBookingStateException extends RuntimeException {

  @Serial private static final long serialVersionUID = -2876932793360242519L;

  public IllegalBookingStateException(String message) {
    super(message);
  }
}
