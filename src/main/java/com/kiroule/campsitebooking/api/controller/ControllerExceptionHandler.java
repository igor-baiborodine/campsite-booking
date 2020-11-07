package com.kiroule.campsitebooking.api.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kiroule.campsitebooking.api.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.api.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.api.exception.IllegalBookingStateException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BookingNotFoundException.class)
  protected ResponseEntity<Object> handleBookingNotFound(BookingNotFoundException ex) {

    ApiError apiError = ApiError.builder()
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

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message(ex.getMessage())
        .build();
    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatus status, WebRequest request) {

    List<String> subErrors = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList());
    subErrors.addAll(ex.getBindingResult().getGlobalErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.toList()));

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message("Validation error")
        .subErrors(subErrors)
        .build();

    log.error("error[Validation error]", ex);
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(javax.validation.ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolation(
      javax.validation.ConstraintViolationException ex) {

    List<String> subErrors = ex.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message("Validation error")
        .subErrors(subErrors)
        .build();

    return buildResponseEntity(apiError);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ServletWebRequest servletWebRequest = (ServletWebRequest) request;
    log.info("{} to {}", servletWebRequest.getHttpMethod(),
        servletWebRequest.getRequest().getServletPath());

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message("Malformed JSON request")
        .build();

    return buildResponseEntity(apiError);
  }

  @ExceptionHandler(StaleObjectStateException.class)
  protected ResponseEntity<Object> handleStaleObjectStateException(StaleObjectStateException ex) {

    ApiError apiError = ApiError.builder()
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

@Data
@Builder
class ApiError {

  private HttpStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private LocalDateTime timestamp;

  private String message;

  @JsonInclude(Include.NON_NULL)
  private List<String> subErrors;

}