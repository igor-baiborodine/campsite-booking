package com.kiroule.campsitebooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.kiroule.campsitebooking.AbstractTest;
import com.kiroule.campsitebooking.exception.BookingNotFoundException;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link BookingServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@RunWith(MockitoJUnitRunner.class)
public class BookingServiceImplTest extends AbstractTest {

  @Mock
  private BookingRepository bookingRepository;

  @InjectMocks
  private BookingServiceImpl bookingService;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void findBookingById_nonExistingBookingId_bookingNotFoundExceptionThrown()
      throws BookingNotFoundException {
    // given
    exception.expect(BookingNotFoundException.class);
    long id = 1L;
    doReturn(Optional.empty()).when(bookingRepository).findById(id);
    // when
    bookingService.findBookingById(id);
    // then
    // BookingNotFoundException is thrown
  }

  @Test
  public void findBookingById_existingBookingId_bookingFound() throws BookingNotFoundException {
    // given
    long id = 1L;
    Booking savedBooking = createBooking(
        LocalDate.of(2018, 10, 1), LocalDate.of(2018, 10, 2)
    );
    doReturn(Optional.of(savedBooking)).when(bookingRepository).findById(id);
    // when
    Booking booking = bookingService.findBookingById(id);
    // then
    assertThat(booking).isEqualTo(savedBooking);
  }
}