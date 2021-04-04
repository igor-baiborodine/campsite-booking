package com.kiroule.campsite.booking.api.controller;

import static com.kiroule.campsite.booking.api.TestHelper.buildBookingDto;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
class BookingControllerTestIT {

  @Autowired
  private BookingRepository bookingRepository;

  @LocalServerPort
  int port;

  private String controllerPath = "/v1/booking";

  @BeforeEach
  void beforeEach() {
    RestAssured.port = port;
    RestAssured.defaultParser = Parser.JSON;
    bookingRepository.deleteAll();
  }

  @Test
  void getActuatorHealth_statusOkAndBodyContainsStatusUp() {
    ValidatableResponse response = given()
        .when().get("/actuator/health")
        .then();
    response.statusCode(HttpStatus.OK.value());
    response.body("status", equalTo("UP"));
  }

  @Test
  void getVacantDates_noBookingsWithinDateRange_datesWithinDateRangeInclusive() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(3);
    // when
    List<String> vacantDates = given()
        .param("start_date", startDate.toString()).param("end_date", endDate.toString())
        .when().get(controllerPath + "/vacant-dates")
        .then().extract().body().as(List.class);
    // then
    List<String> expected = startDate
        .datesUntil(endDate.plusDays(1))
        .map(String::valueOf)
        .collect(Collectors.toList());
    assertThat(vacantDates).isEqualTo(expected);
  }

  @Test
  void getBooking_nonExistingBooking_statusNotFound() {
    UUID nonExistingBookingUuid = UUID.randomUUID();
    given()
        .pathParam("uuid", nonExistingBookingUuid)
        .when().get(controllerPath + "/{uuid}")
        .then().statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void getBooking_existingBooking_bookingFound() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    UUID uuid = UUID.randomUUID();

    BookingDto addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate, uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    // when
    BookingDto foundBooking = given().pathParam("uuid", uuid)
        .when().get(controllerPath + "/{uuid}")
        .as(BookingDto.class);
    // then
    assertThat(foundBooking).isEqualTo(addedBooking);
  }

  @Test
  void addBooking_bookingDatesNotAvailable_statusBadRequest() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate))
        .when().post(controllerPath)
        .then().statusCode(HttpStatus.CREATED.value());
    // when
    ApiError apiError = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate))
        .when().post(controllerPath)
        .as(ApiError.class);
    // then
    assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiError.getMessage()).isEqualTo(
        String.format("No vacant dates available from %s to %s", startDate, endDate));
  }

  @Test
  void addBooking_bookingDatesExceedMaximumStay_statusBadRequest() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(5);
    // when
    ApiError apiError = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate))
        .when().post(controllerPath)
        .as(ApiError.class);
    // then
    assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiError.getMessage()).isEqualTo("Validation error");
  }

  @Test
  void updateBooking_existingBooking_bookingUpdated() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    UUID uuid = UUID.randomUUID();

    BookingDto addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate, uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    addedBooking.setEndDate(endDate.plusDays(1));
    // when
    BookingDto updatedBooking = given()
        .pathParam("uuid", uuid)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{uuid}")
        .as(BookingDto.class);
    // then
    assertThat(updatedBooking.getUuid()).isEqualTo(addedBooking.getUuid());
    assertThat(updatedBooking.getVersion()).isEqualTo(addedBooking.getVersion() + 1);
    assertThat(updatedBooking.getEndDate()).isEqualTo(endDate.plusDays(1));
  }

  @Test
  void updateBooking_existingBookingAndBookingDatesNotAvailable_statusBadRequest() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    UUID uuid = UUID.randomUUID();

    BookingDto addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate, uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    addedBooking.setEndDate(endDate.plusDays(1));
    // other booking
    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(endDate, endDate.plusDays(1), UUID.randomUUID()))
        .when().post(controllerPath)
        .then().statusCode(HttpStatus.CREATED.value());
    // when
    ApiError apiError = given()
        .pathParam("uuid", addedBooking.getUuid())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{uuid}")
        .as(ApiError.class);
    // then
    assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(apiError.getMessage()).isEqualTo(
        String.format("No vacant dates available from %s to %s", startDate, endDate.plusDays(1)));
  }

  @Test
  void updateBooking_bookingWasUpdatedByAnotherTransaction_statusConflict() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    UUID uuid = UUID.randomUUID();

    BookingDto addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate, uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    addedBooking.setEndDate(endDate.plusDays(1));

    given()
        .pathParam("uuid", uuid)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{uuid}")
        .as(BookingDto.class);

    addedBooking.setEndDate(endDate.plusDays(2));
    // when
    ApiError apiError = given()
        .pathParam("uuid", uuid)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{uuid}")
        .as(ApiError.class);
    // then
    assertThat(apiError.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(apiError.getMessage()).isEqualTo(
            "Optimistic locking error - booking was updated by another transaction");
  }

  @Test
  void cancelBooking_existingBooking_bookingCancelled() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);
    UUID uuid = UUID.randomUUID();

    given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(buildBookingDto(startDate, endDate, uuid))
        .when().post(controllerPath)
        .as(BookingDto.class);
    // when
    ValidatableResponse response = given().pathParam("uuid", uuid)
        .when().delete(controllerPath + "/{uuid}").then();
    // then
    response.statusCode(HttpStatus.OK.value());
  }

}