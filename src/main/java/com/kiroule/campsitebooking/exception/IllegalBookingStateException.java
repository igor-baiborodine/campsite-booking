package com.kiroule.campsitebooking.exception;

/**
 * @author Igor Baiborodine
 */
public class IllegalBookingStateException extends RuntimeException {

  public IllegalBookingStateException(String message) {
    super(message);
  }
}
