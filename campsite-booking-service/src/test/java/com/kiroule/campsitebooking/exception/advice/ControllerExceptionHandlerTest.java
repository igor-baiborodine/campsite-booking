package com.kiroule.campsitebooking.exception.advice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

import com.kiroule.campsitebooking.contract.v2.error.ApiError;
import com.kiroule.campsitebooking.exception.BookingDatesNotAvailableException;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class ControllerExceptionHandlerTest {

  ControllerExceptionHandler classUnderTest = new ControllerExceptionHandler();

  @Nested
  class HandleBookingNotFound {

    @Test
    void happy_path() {
      // given
      BookingNotFoundException exception = new BookingNotFoundException("Not found");
      // when
      ResponseEntity<Object> result = classUnderTest.handleBookingNotFound(exception);
      // then
      assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
      assertThat(((ApiError) result.getBody()).getMessage()).isEqualTo("Not found");
    }
  }

  @Nested
  class HandleBookingDatesNotAvailable {
    @Test
    void happy_path() {
      // given
      BookingDatesNotAvailableException exception =
          new BookingDatesNotAvailableException("Dates not available");
      // when
      ResponseEntity<Object> result = classUnderTest.handleBookingDatesNotAvailable(exception);
      // then
      assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
      assertThat(((ApiError) result.getBody()).getMessage()).isEqualTo("Dates not available");
    }
  }

  @Test
  void handleMethodArgumentNotValid() {}

  @Test
  void handleHttpMessageNotReadable() {}

  @Nested
  class handleStaleObjectStateException {
    @Test
    void happy_path() {
      // given
      StaleObjectStateException exception = new StaleObjectStateException("BookingEntity", 1L);
      // when
      ResponseEntity<Object> result = classUnderTest.handleStaleObjectStateException(exception);
      // then
      assertThat(result.getStatusCode()).isEqualTo(CONFLICT);
      var message =
          "Optimistic locking error: BookingEntity with id 1 was updated by another transaction";
      assertThat(((ApiError) result.getBody()).getMessage()).isEqualTo(message);
    }
  }
}
