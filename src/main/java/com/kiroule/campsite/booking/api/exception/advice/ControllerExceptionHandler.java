package com.kiroule.campsite.booking.api.exception.advice;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.kiroule.campsite.booking.api.contract.v2.error.ApiError;
import com.kiroule.campsite.booking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsite.booking.api.exception.BookingNotFoundException;
import com.kiroule.campsite.booking.api.exception.IllegalBookingStateException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Igor Baiborodine
 */
@Slf4j
@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BookingNotFoundException.class)
  protected ResponseEntity<Object> handleBookingNotFound(BookingNotFoundException ex) {

    var apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND)
        .message(ex.getMessage())
        .build();
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({IllegalBookingStateException.class,
      BookingDatesNotAvailableException.class,
      IllegalArgumentException.class})
  protected ResponseEntity<Object> handleBookingDatesNotAvailable(RuntimeException ex) {

    var apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message(ex.getMessage())
        .build();
    return buildResponseEntity(apiError);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    var subErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    subErrors.addAll(ex.getBindingResult().getGlobalErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList());

    var apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message("Validation error")
        .subErrors(subErrors)
        .build();

    log.error("Validation error for {}", ex.getBindingResult().getTarget());
    return buildResponseEntity(apiError);
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
          HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    var servletWebRequest = (ServletWebRequest) request;
    log.info("{} to {}", servletWebRequest.getHttpMethod(),
        servletWebRequest.getRequest().getServletPath());

    var apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message("Malformed JSON request")
        .build();

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<Object> handleStaleObjectStateException(StaleObjectStateException ex) {

    var apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.CONFLICT)
        .message("Optimistic locking error - booking was updated by another transaction")
        .build();

    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

}
