package com.kiroule.campsite.booking.api.controller;

import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.contract.v2.model.ApiError;
import com.kiroule.campsite.booking.api.contract.v2.model.BookingDto;
import com.kiroule.campsite.booking.api.repository.BookingRepository;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("in-memory-db")
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class BookingControllerTestIT {

  @Autowired
  BookingRepository bookingRepository;

  @LocalServerPort
  int port;

  String controllerPath = "/v2/booking";
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
      given()
          .when().get("/actuator/health")
          .then().statusCode(HttpStatus.OK.value()).body("status", equalTo("UP"));
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
      given_dateRangeAndWhenGetVacantDates(1, 3, 1);

      then_assertVacantDatesFound(1, 3);
    }

    private void given_dateRangeAndWhenGetVacantDates(int startPlusDays, int endPlusDays, long campsiteId) {
      vacantDates = given()
          .param("start_date", now.plusDays(startPlusDays).toString())
          .param("end_date", now.plusDays(endPlusDays).toString())
          .param("campsite_id", String.valueOf(campsiteId))
          .when().get(controllerPath + "/vacant-dates")
          .then().extract().body().as(List.class);
    }

    private void then_assertVacantDatesFound(int startPlusDays, int endPlusDays) {
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
      given_existingBooking(1, 2);

      when_getBooking();

      then_assertBookingFound();
    }

    private void when_getBooking() {
      foundBookingDto = given().pathParam("uuid", uuid)
          .when().get(controllerPath + "/{uuid}")
          .as(BookingDto.class);
    }

    private void then_assertBookingFound() {
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
      given_existingBooking(1, 2);

      when_addBookingResultsInApiError(1, 2);

      then_assertApiErrorThrown(HttpStatus.BAD_REQUEST, String.format(
          "No vacant dates available from %s to %s", now.plusDays(1), now.plusDays(2)));
    }

    @Test
    void add_booking__given_booking_duration_exceeds_maximum_stay__then_status_bad_request() {
      // given booking duration exceeds the maximum stay - 3 days

      when_addBookingResultsInApiError(1, 5);

      then_assertApiErrorThrown(HttpStatus.BAD_REQUEST, "Validation error");
    }

    private void when_addBookingResultsInApiError(int startPlusDays, int endPlusDays) {
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
      given_existingBooking(1, 2);
      given_existingBookingEndDateChanged(1);

      when_updateBooking();

      then_assertBookingUpdated();
    }

    @Test
    void given_other_existing_booking_with_same_booking_dates__then_status_bad_request() {
      given_existingBooking(1, 2);
      given_otherExistingBooking(2, 3);
      given_existingBookingEndDateChanged(1);

      when_updateBookingResultsInApiError();

      then_assertApiErrorThrown(HttpStatus.BAD_REQUEST,
          String.format("No vacant dates available from %s to %s",
              existingBookingDto.getStartDate(), existingBookingDto.getEndDate()));
    }

    @Test
    void given_existing_booking_was_updated_by_another_transaction__then_status_conflict() {
      given_existingBooking(1, 2);
      given_existingBookingEndDateChanged(1);
      given_existingBookingUpdatedByAnotherTransaction();
      given_existingBookingEndDateChanged(1);

      when_updateBookingResultsInApiError();

      then_assertApiErrorThrown(HttpStatus.CONFLICT,
          "Optimistic locking error - booking was updated by another transaction");
    }

    private void given_otherExistingBooking(int startPlusDays, int endPlusDays) {
      given()
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays), UUID.randomUUID()))
          .when().post(controllerPath)
          .then().statusCode(HttpStatus.CREATED.value());
    }

    private void given_existingBookingEndDateChanged(int extendByDays) {
      existingBookingDto.setEndDate(existingBookingDto.getEndDate().plusDays(extendByDays));
    }

    private void given_existingBookingUpdatedByAnotherTransaction() {
      given()
          .pathParam("uuid", uuid)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .then().statusCode(HttpStatus.OK.value());
    }

    private void when_updateBooking() {
      updatedBookingDto = given()
          .pathParam("uuid", uuid)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .as(BookingDto.class);
    }

    private void when_updateBookingResultsInApiError() {
      apiError = given()
          .pathParam("uuid", existingBookingDto.getUuid())
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .body(existingBookingDto)
          .when().put(controllerPath + "/{uuid}")
          .as(ApiError.class);
    }

    private void then_assertBookingUpdated() {
      assertAll(
          "updatedBooking",
          () -> assertThat(updatedBookingDto).usingRecursiveComparison().ignoringFields("version")
                  .isEqualTo(existingBookingDto),
          () -> assertThat(updatedBookingDto.getVersion()).isEqualTo(existingBookingDto.getVersion() + 1L));
    }
  }

  @Nested
  class Cancel_Booking {

    @Test
    void given_active_existing_booking__then_booking_canceled() {
      given_existingBooking(1, 2);

      when_bookingCanceledThenStatusOk();
    }

    private void when_bookingCanceledThenStatusOk() {
      given().pathParam("uuid", uuid)
          .when().delete(controllerPath + "/{uuid}")
          .then().statusCode(HttpStatus.OK.value());
    }
  }

  private void given_existingBooking(int startPlusDays, int endPlusDays) {
    existingBookingDto = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(now.plusDays(startPlusDays), now.plusDays(endPlusDays), uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);

    assumeThat(existingBookingDto.getId()).isNotNull();
    assumeThat(existingBookingDto.getVersion()).isEqualTo(0L);
    assumeThat(existingBookingDto.isActive()).isTrue();
  }

  private void then_assertApiErrorThrown(HttpStatus expectedHttpStatus, String expectedMessage) {
    assertThat(apiError.getStatus()).isEqualTo(expectedHttpStatus);
    assertThat(apiError.getMessage()).isEqualTo(expectedMessage);
  }
}