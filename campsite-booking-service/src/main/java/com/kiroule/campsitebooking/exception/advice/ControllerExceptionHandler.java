package com.kiroule.campsitebooking.exception.advice;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsitebooking.contract.v2.error.ApiError;
import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
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

    var apiError =
        ApiError.builder().timestamp(now()).status(NOT_FOUND).message(ex.getMessage()).build();
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({BookingDatesNotAvailableException.class, IllegalArgumentException.class})
  protected ResponseEntity<Object> handleBookingDatesNotAvailable(RuntimeException ex) {

    var apiError =
        ApiError.builder().timestamp(now()).status(BAD_REQUEST).message(ex.getMessage()).build();
    return buildResponseEntity(apiError);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    var subErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());
    subErrors.addAll(
        ex.getBindingResult().getGlobalErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList());

    var apiError =
        ApiError.builder()
            .timestamp(now())
            .status(BAD_REQUEST)
            .message("Validation error")
            .subErrors(subErrors)
            .build();

    log.error("Validation error for {}", ex.getBindingResult().getTarget());
    return buildResponseEntity(apiError);
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var servletWebRequest = (ServletWebRequest) request;
    log.info(
        "{} to {}",
        servletWebRequest.getHttpMethod(),
        servletWebRequest.getRequest().getServletPath());

    var apiError =
        ApiError.builder()
            .timestamp(now())
            .status(BAD_REQUEST)
            .message("Malformed JSON request")
            .build();

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<Object> handleStaleObjectStateException(StaleObjectStateException ex) {

    var message =
        format(
            "Optimistic locking error: %s with id %s was updated by another transaction",
            ex.getEntityName(), ex.getIdentifier());
    var apiError = ApiError.builder().timestamp(now()).status(CONFLICT).message(message).build();

    return buildResponseEntity(apiError);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}