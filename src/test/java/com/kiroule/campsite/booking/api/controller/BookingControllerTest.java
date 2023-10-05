package com.kiroule.campsite.booking.api.controller;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscores;
import com.kiroule.campsite.booking.api.service.BookingService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
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
@DisplayNameGeneration(CustomReplaceUnderscores.class)
public class BookingControllerTest {

  @Mock
  BookingService bookingService;

  @InjectMocks
  BookingController classUnderTest;

  LocalDate now;

  Long campsiteId = 1L;

  @BeforeEach
  void beforeEach() {
    now = LocalDate.now();
  }

  @Nested
  class GetVacantDates {

    LocalDate startDate;
    LocalDate endDate;
    List<LocalDate> vacantDates;

    @Test
    void when_start_date_is_null__then_start_date_is_now_plus_one_day() {
      given_startDateAndEndDate(null, now.plusDays(15));
      given_bookingServiceFindVacantDates(singletonList(now.plusDays(5)));

      when_getVacantDates();

      then_assertVacantDates(now.plusDays(1), now.plusDays(15), singletonList(now.plusDays(5)));
    }

    @Test
    void when_end_date_is_null__then_end_date_is_start_date_plus_one_month() {
      given_startDateAndEndDate(now.plusDays(15), null);
      given_bookingServiceFindVacantDates(singletonList(now.plusDays(17)));

      when_getVacantDates();

      then_assertVacantDates(
          now.plusDays(15), now.plusDays(15).plusMonths(1), singletonList(now.plusDays(17)));
    }

    private void given_startDateAndEndDate(LocalDate startDate, LocalDate endDate) {
      this.startDate = startDate;
      this.endDate = endDate;
    }

    private void given_bookingServiceFindVacantDates(List<LocalDate> vacantDates) {
      doReturn(vacantDates).when(bookingService).findVacantDays(any(), any(), any());
    }

    private void when_getVacantDates() {
      vacantDates = classUnderTest.getVacantDates(startDate, endDate, campsiteId).getBody();
    }

    private void then_assertVacantDates(
        LocalDate startDate, LocalDate endDate, List<LocalDate> vacantDates) {
      assertThat(this.vacantDates).isEqualTo(vacantDates);
      verify(bookingService).findVacantDays(startDate, endDate, campsiteId);
    }
  }
}
