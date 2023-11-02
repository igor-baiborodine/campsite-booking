package com.kiroule.campsitebooking.controller;

import static java.time.LocalDate.now;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.kiroule.campsitebooking.service.BookingService;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link BookingController}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

  @Mock
  BookingService bookingService;

  @InjectMocks BookingController classUnderTest;

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
          classUnderTest.getVacantDates(startDate, endDate, CAMPSITE_ID).getBody();
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
          classUnderTest.getVacantDates(startDate, endDate, CAMPSITE_ID).getBody();
      // then
      assertThat(result).isEqualTo(singletonList(now.plusDays(17)));
      verify(bookingService).findVacantDates(startDate, startDate.plusMonths(1), CAMPSITE_ID);
    }
  }
}
