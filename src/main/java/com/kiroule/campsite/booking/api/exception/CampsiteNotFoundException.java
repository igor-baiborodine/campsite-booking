package com.kiroule.campsite.booking.api.exception;

/**
 * @author Igor Baiborodine
 */
public class CampsiteNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CampsiteNotFoundException(String message) {
    super(message);
  }
}
