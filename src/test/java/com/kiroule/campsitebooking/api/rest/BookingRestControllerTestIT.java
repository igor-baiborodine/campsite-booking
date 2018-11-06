package com.kiroule.campsitebooking.api.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import com.kiroule.campsitebooking.TestHelper;
import com.kiroule.campsitebooking.model.Booking;
import com.kiroule.campsitebooking.repository.BookingRepository;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ValidatableResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingRestControllerTestIT {

  @Autowired
  private BookingRepository bookingRepository;

  @Autowired
  private TestHelper helper;

  @LocalServerPort
  int port;

  @Value("${server.servlet.context-path}")
  private String basePath;

  private String controllerPath = "/api/bookings";

  @Before
  public void setUp() {
    RestAssured.port = port;
    RestAssured.basePath = basePath;
    RestAssured.defaultParser = Parser.JSON;
    bookingRepository.deleteAll();
  }

  @Test
  public void getActuatorHealth_statusOkAndBodyContainsStatusUp() {
    ValidatableResponse response =
        given()
            .when().get("/actuator/health")
            .then();
    response.statusCode(HttpStatus.OK.value());
    response.body("status", equalTo("UP"));
  }

  @Test
  public void getVacantDates_noBookingsWithinDateRange_datesWithinDateRangeInclusive() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(3);
    // when
    List<String> vacantDates = Lists.newArrayList();
    vacantDates = given()
        .param("start_date", startDate.toString()).param("end_date", endDate.toString())
        .when().get(controllerPath + "/vacant-dates")
        .then().extract().body().as(vacantDates.getClass());
    // then
    List<String> expected = startDate
        .datesUntil(endDate.plusDays(1))
        .map(String::valueOf)
        .collect(Collectors.toList());
    Assertions.assertThat(vacantDates).isEqualTo(expected);
  }

  @Test
  public void getBooking_nonExistingBooking_statusNotFound() {
    Long nonExistingBookingId = Long.MAX_VALUE;
    given().pathParam("id", nonExistingBookingId)
        .when().get(controllerPath + "/{id}")
        .then().statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  public void getBooking_existingBooking_bookingFound() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    Booking addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .as(Booking.class);
    // when
    Booking foundBooking = given().pathParam("id", addedBooking.getId())
        .when().get(controllerPath + "/{id}")
        .as(Booking.class);
    // then
    Assertions.assertThat(foundBooking).isEqualTo(addedBooking);
  }

  @Test
  public void addBooking_bookingDatesNotAvailable_statusBadRequest() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .then().statusCode(HttpStatus.CREATED.value());
    // when
    ApiError apiError = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .as(ApiError.class);
    // then
    Assertions.assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(apiError.getMessage()).isEqualTo(
        String.format("No vacant dates available from %s to %s", startDate, endDate));
  }

  @Test
  public void updateBooking_existingBooking_bookingUpdated() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    Booking addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .as(Booking.class);
    addedBooking.setEndDate(endDate.plusDays(1));
    // when
    Booking updatedBooking = given().pathParam("id", addedBooking.getId())
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{id}")
        .as(Booking.class);
    // then
    Assertions.assertThat(updatedBooking.getVersion()).isEqualTo(addedBooking.getVersion() + 1);
    Assertions.assertThat(updatedBooking.getEndDate()).isEqualTo(endDate.plusDays(1));
  }

  @Test
  public void updateBooking_existingBookingAndBookingDatesNotAvailable_statusBadRequest() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    Booking addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .as(Booking.class);
    addedBooking.setEndDate(endDate.plusDays(1));

    // other booking
    given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(endDate, endDate.plusDays(1)))
        .when().post(controllerPath)
        .then().statusCode(HttpStatus.CREATED.value());
    // when
    ApiError apiError = given().pathParam("id", addedBooking.getId())
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(addedBooking)
        .when().put(controllerPath + "/{id}")
        .as(ApiError.class);
    // then
    Assertions.assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(apiError.getMessage()).isEqualTo(
        String.format("No vacant dates available from %s to %s", startDate, endDate.plusDays(1)));
  }

  @Test
  public void cancelBooking_existingBooking_bookingCancelled() {
    // given
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(2);

    Booking addedBooking = given()
        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
        .body(helper.buildBooking(startDate, endDate))
        .when().post(controllerPath)
        .as(Booking.class);
    // when
    ValidatableResponse response = given().pathParam("id", addedBooking.getId())
        .when().delete(controllerPath + "/{id}").then();
    // then
    response.statusCode(HttpStatus.NO_CONTENT.value());
  }

}