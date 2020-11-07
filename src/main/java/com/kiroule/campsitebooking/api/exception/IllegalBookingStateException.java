package com.kiroule.campsitebooking.api.exception;

/**
 * @author Igor Baiborodine
 */
public class IllegalBookingStateException extends RuntimeException {

  private static final long serialVersionUID = -5294679053437468750L;

  public IllegalBookingStateException(String message) {
    super(message);
  }
}
