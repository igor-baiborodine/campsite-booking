package com.kiroule.campsitebooking.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsitebooking.TestHelper;
import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("hsqldb")
public class BookingServiceImplTestIT {

  @Autowired
  private TestHelper helper;

  @Autowired
  private BookingService bookingService;

  @Test
  public void cancelBooking_existingActiveBooking_bookingCancelled() {
    // given
    Booking savedBooking = bookingService.createBooking(helper.buildBooking(
        LocalDate.now(), LocalDate.now().plusDays(1)));
    assertThat(savedBooking.getId()).isNotNull();
    assertThat(savedBooking).hasFieldOrPropertyWithValue("active", true);
    // when
    boolean cancelled = bookingService.cancelBooking(savedBooking.getId());
    // then
    assertThat(cancelled).isTrue();
  }

}
