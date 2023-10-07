package com.kiroule.campsite.booking.api.model.mapper;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static com.kiroule.campsite.booking.api.TestHelper.EMAIL;
import static com.kiroule.campsite.booking.api.TestHelper.FULL_NAME;
import static com.kiroule.campsite.booking.api.TestHelper.buildBooking;
import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.kiroule.campsite.booking.api.TestHelper;
import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.exception.CampsiteNotFoundException;
import com.kiroule.campsite.booking.api.mapper.BookingMapperImpl;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.service.CampsiteService;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

  @Mock CampsiteService campsiteService;

  @InjectMocks BookingMapperImpl classUnderTest;

  LocalDate now;
  UUID uuid;
  BookingDto bookingDto;
  Campsite campsite;
  Booking booking;

  @BeforeEach
  void beforeEach() {
    now = LocalDate.now();
    uuid = UUID.randomUUID();
    bookingDto = null;
    campsite = null;
    booking = null;
  }

  @Nested
  class MapToBooking {

    @Test
    void happy_path() {
      given_bookingDto(1, 2);
      given_campsite();

      when_mapToBooking();

      then_assertBooking(1, 2);
    }

    @Test
    void given_non_existing_campsite_id__then_campsite_not_found_exception_thrown() {
      given_bookingDto(1, 2);
      given_noCampsiteFound();

      when_mapToBooking_then_assertExceptionThrown(CampsiteNotFoundException.class);
    }

    @Test
    void given_null_booking_dto__then_null_returned() {
      given_nullBookingDto();

      when_mapToBooking();

      then_assertNullBooking();
    }

    private void given_nullBookingDto() {
      bookingDto = null;
    }

    private void given_bookingDto(int startPlusDays, int endPlusDays) {
      bookingDto = buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    }

    private void given_campsite() {
      campsite = TestHelper.buildCampsite();
      doReturn(campsite).when(campsiteService).findById(any());
    }

    private void given_noCampsiteFound() {
      doThrow(CampsiteNotFoundException.class).when(campsiteService).findById(any());
    }

    private void when_mapToBooking() {
      booking = classUnderTest.toBooking(bookingDto);
    }

    private void then_assertBooking(int startPlusDays, int endPlusDays) {
      assertThat(booking.getCampsite()).usingRecursiveComparison().isEqualTo(campsite);
      assertThat(booking.getId()).isNull();
      assertThat(booking.getUuid()).isEqualTo(uuid);

      assertThat(booking.getVersion()).isNull();
      assertThat(booking.getEmail()).isEqualTo(EMAIL);
      assertThat(booking.getFullName()).isEqualTo(FULL_NAME);

      assertThat(booking.getStartDate()).isEqualTo(now.plusDays(startPlusDays));
      assertThat(booking.getEndDate()).isEqualTo(now.plusDays(endPlusDays));
      assertThat(booking.isActive()).isTrue();

      verify(campsiteService).findById(CAMPSITE_ID);
    }

    private void when_mapToBooking_then_assertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> campsiteService.findById(any()));
    }

    private void then_assertNullBooking() {
      assertThat(booking).isNull();
    }
  }

  @Nested
  class MapToBookingDto {

    @Test
    void happy_path() {
      given_booking(1, 2);

      when_mapToBookingDto();

      then_assertBookingDto(1, 2);
    }

    @Test
    void given_null_booking_dto__then_null_returned() {
      given_nullBooking();

      when_mapToBookingDto();

      then_assertNullBookingDto();
    }

    private void given_nullBooking() {
      booking = null;
    }

    private void given_booking(int startPlusDays, int endPlusDays) {
      booking = buildBooking(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid);
    }

    private void when_mapToBookingDto() {
      bookingDto = classUnderTest.toBookingDto(booking);
    }

    private void then_assertBookingDto(int startPlusDays, int endPlusDays) {
      assertThat(bookingDto.getCampsiteId()).isEqualTo(CAMPSITE_ID);
      assertThat(bookingDto.getUuid()).isEqualTo(uuid);

      assertThat(bookingDto.getVersion()).isNull();
      assertThat(bookingDto.getEmail()).isEqualTo(EMAIL);
      assertThat(bookingDto.getFullName()).isEqualTo(FULL_NAME);

      assertThat(bookingDto.getStartDate()).isEqualTo(now.plusDays(startPlusDays));
      assertThat(bookingDto.getEndDate()).isEqualTo(now.plusDays(endPlusDays));
      assertThat(bookingDto.isActive()).isTrue();
    }

    private void then_assertNullBookingDto() {
      assertThat(bookingDto).isNull();
    }
  }
}
