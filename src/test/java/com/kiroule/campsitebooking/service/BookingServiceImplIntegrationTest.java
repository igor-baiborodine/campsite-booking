package com.kiroule.campsitebooking.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsitebooking.AbstractTest;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@Transactional
@ActiveProfiles("hsqldb")
public class BookingServiceImplIntegrationTest extends AbstractTest {

  @Autowired
  private BookingService bookingService;

  @Test
  public void cancelBooking_existingActiveBooking_bookingCancelled()
      throws BookingNotFoundException {
    // given
    Booking savedBooking = bookingService.saveBooking(createBooking(
        LocalDate.of(2018, 10, 3),
        LocalDate.of(2018, 10, 4)));
    assertThat(savedBooking.getId()).isNotNull();
    assertThat(savedBooking).hasFieldOrPropertyWithValue("active", true);
    // when
    boolean cancelled = bookingService.cancelBooking(savedBooking.getId());
    // then
    assertThat(cancelled).isTrue();
  }

}
