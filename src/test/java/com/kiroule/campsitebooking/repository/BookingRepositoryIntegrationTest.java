package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for {@link BookingRepository}.
 *
 * @author Igor Baiborodine
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("hsqldb")
public class BookingRepositoryIntegrationTest {

  @Autowired
  private BookingRepository repository;

  @Test
  public void findById_savedBooking_savedBookingFound() {
    Booking booking = repository.save(createBooking(
        LocalDate.of(2018, 10, 1),
        LocalDate.of(2018, 10, 2)));

    Assertions.assertThat(repository.findById(booking.getId())).hasValue(booking);
  }

  private Booking createBooking(LocalDate startDate, LocalDate endDate) {
    return Booking.builder()
        .fullName("Bender Rodriguez")
        .email("brodriguez@futurama.com")
        .startDate(startDate)
        .endDate(endDate)
        .active(true)
        .build();
  }
}
