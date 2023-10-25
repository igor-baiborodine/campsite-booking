package com.kiroule.campsite.booking.api.exception;

import java.io.Serial;

/**
 * @author Igor Baiborodine
 */
public class CampsiteNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 5845737860525786782L;

  public CampsiteNotFoundException(String message) {
    super(message);
  }
}
