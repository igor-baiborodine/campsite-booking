package com.kiroule.campsitebooking.api.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.exception.IllegalBookingStateException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Igor Baiborodine
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BookingNotFoundException.class)
  protected ResponseEntity<Object> handleBookingNotFound(BookingNotFoundException ex) {

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND)
        .message(ex.getMessage())
        .build();
    return buildResponseEntity(apiError);
  }

  @ExceptionHandler({IllegalBookingStateException.class, BookingDatesNotAvailableException.class})
  protected ResponseEntity<Object> handleBookingDatesNotAvailable(RuntimeException ex) {

    ApiError apiError = ApiError.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST)
        .message(ex.getMessage())
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