package com.kiroule.campsite.booking.api.controller;

import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.contract.v1.model.ApiError;
import com.kiroule.campsite.booking.api.contract.v1.model.BookingDto;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class BookingControllerTestIT {

  @Autowired
  BookingRepository bookingRepository;

  @LocalServerPort
  int port;

  String controllerPath = "/v1/booking";
  UUID uuid;
  LocalDate now;

  BookingDto existingBookingDto;
  ApiError apiError;

  @BeforeEach
  void beforeEach() {
    RestAssured.port = port;
    RestAssured.defaultParser = Parser.JSON;

    uuid = UUID.randomUUID();
    now = LocalDate.now();
    existingBookingDto = null;
    apiError = null;

    bookingRepository.deleteAll();
  }

  @Nested
  class Get_Actuator_Health {
    @Test
    void given_service_is_running__then_status_OK() {
      ValidatableResponse response = given()
          .when().get("/actuator/health")
          .then();
      response.statusCode(HttpStatus.OK.value());
      response.body("status", equalTo("UP"));
    }
  }

  @Nested
  class Get_Vacant_Dates {

    List<String> vacantDates;

    @BeforeEach
    void beforeEach() {
      vacantDates = null;
    }

    @Test
    void when_no_bookings_found_for_date_range__then_all_dates_within_date_range_inclusive() {
      givenDateRangeAndWhenGetVacantDates(1, 3);

      thenAssertVacantDatesFound(1, 3);
    }

    private void givenDateRangeAndWhenGetVacantDates(int startPlusDays, int endPlusDays) {
      vacantDates = given()
          .param("start_date", now.plusDays(startPlusDays).toString())
          .param("end_date", now.plusDays(endPlusDays).toString())
          .when().get(controllerPath + "/vacant-dates")
          .then().extract().body().as(List.class);
    }

    private void thenAssertVacantDatesFound(int startPlusDays, int endPlusDays) {
      List<String> expected = now.plusDays(startPlusDays)
          .datesUntil(now.plusDays(endPlusDays + 1))
          .map(String::valueOf)
          .collect(Collectors.toList());
      assertThat(vacantDates).hasSize(expected.size()).hasSameElementsAs(expected);
    }
  }

  @Nested
  class Get_Booking {

    BookingDto foundBookingDto;

    @BeforeEach
    void beforeEach() {
      foundBookingDto = null;
    }

    @Test
    void given_non_existing_booking_uuid__then_status_not_found() {
      given()
          .pathParam("uuid", UUID.randomUUID())
          .when().get(controllerPath + "/{uuid}")
          .then().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void given_existing_booking_uuid__then_booking_found() {
      givenExistingBooking(1, 2);

      whenGetBooking();

      thenAssertBookingFound();
    }

    private void whenGetBooking() {
      foundBookingDto = given().pathParam("uuid", uuid)
          .when().get(controllerPath + "/{uuid}")
          .as(BookingDto.class);
    }

    private void thenAssertBookingFound() {
      assertThat(foundBookingDto).isEqualToIgnoringGivenFields(existingBookingDto, "id", "version");
    }
  }

  @Nested
  class Add_Booking {

    BookingDto newBookingDto;

    @BeforeEach
    void beforeEach() {
      newBookingDto = null;
    }

    @Test
    void given_booking_dates_not_available__then_status_bad_request() {
      givenExistingBooking(1, 2);

      whenAddBookingResultsInApiError(1, 2);

      thenAssertApiErrorThrown(HttpStatus.BAD_REQUEST, String.format(
          "No vacant dates available from %s to %s", now.plusDays(1), now.plusDays(2)));
    }

    @Test
    void add_booking__given_booking_duration_exceeds_maximum_stay__then_status_bad_request() {
      // given booking duration exceeds the maximum stay - 3 days

      whenAddBookingResultsInApiError(1, 5);

      thenAssertApiErrorThrown(HttpStatus.BAD_REQUEST, "Validation error");
    }

    private void whenAddBookingResultsInApiError(int startPlusDays, int endPlusDays) {
      apiError = given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays)))
          .when().post(controllerPath)
          .as(ApiError.class);
    }
  }

  @Nested
  class Update_Booking {

    BookingDto updatedBookingDto;

    @BeforeEach
    void beforeEach() {
      updatedBookingDto = null;
    }

    @Test
    void given_existing_booking_with_end_date_changed__then_booking_updated() {
      givenExistingBooking(1, 2);
      givenExistingBookingEndDateChanged(1);

      whenUpdateBooking();

      thenAssertBookingUpdated();
    }

    @Test
    void given_other_existing_booking_with_same_booking_dates__then_status_bad_request() {
      givenExistingBooking(1, 2);
      givenOtherExistingBooking(2, 3);
      givenExistingBookingEndDateChanged(1);

      whenUpdateBookingResultsInApiError();

      thenAssertApiErrorThrown(HttpStatus.BAD_REQUEST,
          String.format("No vacant dates available from %s to %s",
              existingBookingDto.getStartDate(), existingBookingDto.getEndDate()));
    }

    @Test
    void given_existing_booking_was_updated_by_another_transaction__then_status_conflict() {
      givenExistingBooking(1, 2);
      givenExistingBookingEndDateChanged(1);
      givenExistingBookingUpdatedByAnotherTransaction();
      givenExistingBookingEndDateChanged(1);

      whenUpdateBookingResultsInApiError();

      thenAssertApiErrorThrown(HttpStatus.CONFLICT,
          "Optimistic locking error - booking was updated by another transaction");
    }

    private void givenOtherExistingBooking(int startPlusDays, int endPlusDays) {
      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays), UUID.randomUUID()))
          .when().post(controllerPath)
          .then().statusCode(HttpStatus.CREATED.value());
    }

    private void givenExistingBookingEndDateChanged(int extendByDays) {
      existingBookingDto.setEndDate(existingBookingDto.getEndDate().plusDays(extendByDays));
    }

    private void givenExistingBookingUpdatedByAnotherTransaction() {
      given()
          .pathParam("uuid", uuid)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .then().statusCode(HttpStatus.OK.value());
    }

    private void whenUpdateBooking() {
      updatedBookingDto = given()
          .pathParam("uuid", uuid)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .as(BookingDto.class);
    }

    private void whenUpdateBookingResultsInApiError() {
      apiError = given()
          .pathParam("uuid", existingBookingDto.getUuid())
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .as(ApiError.class);
    }

    private void thenAssertBookingUpdated() {
      assertAll("updatedBooking",
          () -> assertThat(updatedBookingDto)
              .isEqualToIgnoringGivenFields(existingBookingDto, "version"),
          () -> assertThat(updatedBookingDto.getVersion())
              .isEqualTo(existingBookingDto.getVersion() + 1L)
      );
    }
  }

  @Nested
  class Cancel_Booking {
    @Test
    void given_active_existing_booking__then_booking_canceled() {
      givenExistingBooking(1, 2);

      whenBookingCanceledThenStatusOk();
    }

    private void whenBookingCanceledThenStatusOk() {
      given().pathParam("uuid", uuid)
          .when().delete(controllerPath + "/{uuid}")
          .then().statusCode(HttpStatus.OK.value());
    }
  }

  private void givenExistingBooking(int startPlusDays, int endPlusDays) {
    existingBookingDto = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    assumeThat(existingBookingDto.getId()).isNotNull();
    assumeThat(existingBookingDto.getVersion()).isEqualTo(0L);
    assumeThat(existingBookingDto.isActive()).isTrue();
  }

  private void thenAssertApiErrorThrown(HttpStatus expectedHttpStatus, String expectedMessage) {
    assertThat(apiError.getStatus()).isEqualTo(expectedHttpStatus);
    assertThat(apiError.getMessage()).isEqualTo(expectedMessage);
  }
}