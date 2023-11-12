package com.kiroule.campsitebooking.controller;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import com.kiroule.campsitebooking.service.BookingService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link BookingApiControllerImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
public class BookingApiControllerTest {

  @Mock
  BookingService bookingService;

  @InjectMocks
  BookingApiControllerImpl classUnderTest;

  @Nested
  class GetVacantDates {

    private static final Long CAMPSITE_ID = 1L;

    @Test
    void when_start_date_is_null__then_start_date_is_now_plus_one_day() {
      // given
      LocalDate now = now();
      LocalDate startDate = null;
      LocalDate endDate = now.plusDays(15);
      doReturn(singletonList(now.plusDays(10)))
          .when(bookingService)
          .findVacantDates(any(), any(), any());
      // when
      List<LocalDate> result =
          classUnderTest.getVacantDates(CAMPSITE_ID, startDate, endDate).getBody();
      // then
      assertThat(result).isEqualTo(singletonList(now.plusDays(10)));
      verify(bookingService).findVacantDates(now.plusDays(1), endDate, CAMPSITE_ID);
    }

    @Test
    void when_end_date_is_null__then_end_date_is_start_date_plus_one_month() {
      // given
      LocalDate now = now();
      LocalDate startDate = now.plusDays(15);
      LocalDate endDate = null;
      doReturn(singletonList(now.plusDays(17)))
          .when(bookingService)
          .findVacantDates(any(), any(), any());
      // when
      List<LocalDate> result =
          classUnderTest.getVacantDates(CAMPSITE_ID, startDate, endDate).getBody();
      // then
      assertThat(result).isEqualTo(singletonList(now.plusDays(17)));
      verify(bookingService).findVacantDates(startDate, startDate.plusMonths(1), CAMPSITE_ID);
    }
  }


  @Nested
  class CancelBooking {

    @Test
    void happyPath() {
      // given
      UUID uuid = randomUUID();
      when(bookingService.cancelBooking(any())).thenReturn(true);
      // when
      ResponseEntity<Void> result = classUnderTest.cancelBooking(uuid);
      // then
      assertThat(result.getStatusCode()).isEqualTo(OK);
      verify(bookingService).cancelBooking(uuid);
    }

    @Test
    void givenBookingIsNotCancelled_thenBadRequestIsReturned() {
      UUID uuid = randomUUID();
      when(bookingService.cancelBooking(any())).thenReturn(false);
      // when
      ResponseEntity<Void> result = classUnderTest.cancelBooking(uuid);
      // then
      assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
      verify(bookingService).cancelBooking(uuid);
    }
  }
}
