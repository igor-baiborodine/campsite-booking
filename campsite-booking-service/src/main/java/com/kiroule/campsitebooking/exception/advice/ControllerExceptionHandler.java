package com.kiroule.campsitebooking.exception.advice;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsitebooking.api.v2.dto.ApiErrorDto;
import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.Date;
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

    var apiErrorDto =
        ApiErrorDto.builder()
            .timestamp(Date.from(now()))
            .status(NOT_FOUND.value())
            .message(ex.getMessage())
            .subErrors(emptyList())
            .build();
    return buildResponseEntity(apiErrorDto);
  }

  @ExceptionHandler({
    ConstraintViolationException.class,
    IllegalArgumentException.class,
    BookingDatesNotAvailableException.class
  })
  protected ResponseEntity<Object> handleServiceMethodArgumentNotValid(RuntimeException ex) {

    var apiErrorDto =
        ApiErrorDto.builder()
            .timestamp(Date.from(now()))
            .status(BAD_REQUEST.value())
            .message(ex.getMessage())
            .subErrors(emptyList())
            .build();
    return new ResponseEntity<>(apiErrorDto, HttpStatus.valueOf(apiErrorDto.getStatus()));
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

    var apiErrorDto =
        ApiErrorDto.builder()
            .timestamp(Date.from(now()))
            .status(BAD_REQUEST.value())
            .message("Validation error")
            .subErrors(subErrors)
            .build();

    log.error("Validation error for {}", ex.getBindingResult().getTarget());
    return buildResponseEntity(apiErrorDto);
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

    var apiErrorDto =
        ApiErrorDto.builder()
            .timestamp(Date.from(now()))
            .status(BAD_REQUEST.value())
            .message("Malformed JSON request")
            .subErrors(emptyList())
            .build();

    return buildResponseEntity(apiErrorDto);
  }

  @ExceptionHandler(StaleObjectStateException.class)
  public ResponseEntity<Object> handleStaleObjectStateException(StaleObjectStateException ex) {

    var message =
        format(
            "Optimistic locking error: %s with id %s was updated by another transaction",
            ex.getEntityName(), ex.getIdentifier());
    var apiErrorDto =
        ApiErrorDto.builder()
            .timestamp(Date.from(now()))
            .status(CONFLICT.value())
            .message(message)
            .subErrors(emptyList())
            .build();

    return buildResponseEntity(apiErrorDto);
  }

  private ResponseEntity<Object> buildResponseEntity(ApiErrorDto apiError) {
    return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
  }
}
