package com.kiroule.campsitebooking.controller;

import static com.kiroule.campsitebooking.TestDataHelper.nextBookingDto;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiroule.campsitebooking.BaseIT;
import com.kiroule.campsitebooking.TestDataHelper;
import com.kiroule.campsitebooking.api.v2.dto.ApiErrorDto;
import com.kiroule.campsitebooking.api.v2.dto.BookingDto;
import com.kiroule.campsitebooking.repository.BookingRepository;
import com.kiroule.campsitebooking.repository.entity.BookingEntity;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

class BookingApiControllerIT extends BaseIT {

  private static final String BASE_PATH = "/api/v2/booking";

  @Autowired BookingRepository bookingRepository;

  @Autowired TestDataHelper testDataHelper;

  @Autowired ObjectMapper objectMapper;

  @LocalServerPort int port;

  @BeforeEach
  void beforeEach() {
    RestAssured.port = port;
    RestAssured.defaultParser = Parser.JSON;
  }

  @Nested
  class GetActuatorHealth {
    @Test
    void happy_path() {
      given()
          .when()
          .get("/actuator/health")
          .then()
          .statusCode(OK.value())
          .body("status", equalTo("UP"));
    }
  }

  @Nested
  class GetVacantDates {

    @Test
    void happy_path() {
      // given
      LocalDate now = now();
      // when
      List<String> result =
          given()
              .param("start_date", now.plusDays(1).toString())
              .param("end_date", now.plusDays(3).toString())
              .param("campsite_id", "1")
              .when()
              .get(BASE_PATH + "/vacant-dates")
              .then()
              .extract()
              .body()
              .as(List.class);
      // then
      List<String> expected =
          now.plusDays(1).datesUntil(now.plusDays(4)).map(String::valueOf).toList();
      assertThat(result).containsExactlyElementsOf(expected);
    }
  }

  @Nested
  class GetBooking {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      // when
      BookingDto result =
          given()
              .pathParam("uuid", bookingEntity.getUuid())
              .when()
              .get(BASE_PATH + "/{uuid}")
              .as(BookingDto.class);
      // then
      assertThat(result).usingRecursiveComparison().isEqualTo(bookingEntity);
    }

    @Test
    void given_non_existing_booking_uuid__then_status_not_found() {
      // given
      UUID nonExistingUuid = randomUUID();
      // when
      given()
          .pathParam("uuid", nonExistingUuid)
          .when()
          .get(BASE_PATH + "/{uuid}")
          .then()
          .statusCode(NOT_FOUND.value());
    }
  }

  @Nested
  class AddBooking {

    @Test
    void given_booking_dates_not_available__then_status_bad_request() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .uuid(null)
              .version(null)
              .campsiteId(campsiteEntity.getId())
              .startDate(bookingEntity.getStartDate())
              .endDate(bookingEntity.getEndDate())
              .build();
      // when
      ApiErrorDto result =
          given()
              .contentType(APPLICATION_JSON_VALUE)
              .body(bookingDto)
              .when()
              .post(BASE_PATH)
              .as(ApiErrorDto.class);
      // then
      assertThat(result.getStatus()).isEqualTo(BAD_REQUEST.value());
      String message =
          format(
              "No vacant dates available from %s to %s",
              bookingDto.getStartDate(), bookingDto.getEndDate());
      assertThat(result.getMessage()).isEqualTo(message);
    }

    @Test
    void given_booking_duration_exceeds_maximum_stay__then_status_bad_request() {
      // given
      LocalDate now = now();
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .uuid(null)
              .campsiteId(campsiteEntity.getId())
              .startDate(now.plusDays(1))
              .endDate(now.plusDays(5))
              .build();
      // when
      ApiErrorDto result =
          given()
              .contentType(APPLICATION_JSON_VALUE)
              .body(bookingDto)
              .when()
              .post(BASE_PATH)
              .as(ApiErrorDto.class);
      // then
      assertThat(result.getStatus()).isEqualTo(BAD_REQUEST.value());
      assertThat(result.getMessage()).isEqualTo("createBooking.booking: Booking stay length must be less or equal to three days");
      assertThat(result.getSubErrors()).isEmpty();
    }
  }

  @Nested
  class UpdateBooking {

    BookingDto updatedBookingDto;

    @BeforeEach
    void beforeEach() {
      updatedBookingDto = null;
    }

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .uuid(bookingEntity.getUuid())
              .version(bookingEntity.getVersion())
              .campsiteId(bookingEntity.getCampsiteId())
              .startDate(bookingEntity.getStartDate().plusDays(1))
              .endDate(bookingEntity.getEndDate().plusDays(2))
              .active(bookingEntity.isActive())
              .build();
      // when
      BookingDto result =
          given()
              .pathParam("uuid", bookingDto.getUuid())
              .contentType(APPLICATION_JSON_VALUE)
              .body(bookingDto)
              .when()
              .put(BASE_PATH + "/{uuid}")
              .as(BookingDto.class);
      // then
      assertThat(result).usingRecursiveComparison().ignoringFields("version").isEqualTo(bookingDto);
      assertThat(result.getVersion()).isEqualTo(1L);
    }

    @Test
    void given_other_existing_booking_with_same_booking_dates__then_status_bad_request() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity1 = testDataHelper.createBookingEntity(campsiteEntity.getId());
      BookingEntity bookingEntity2 = testDataHelper.createBookingEntity(campsiteEntity.getId());
      BookingDto bookingDto1 =
          nextBookingDto().toBuilder()
              .uuid(bookingEntity1.getUuid())
              .campsiteId(bookingEntity1.getCampsiteId())
              .startDate(bookingEntity2.getStartDate())
              .endDate(bookingEntity2.getEndDate())
              .active(bookingEntity1.isActive())
              .build();
      // when
      ApiErrorDto result =
          given()
              .pathParam("uuid", bookingDto1.getUuid())
              .contentType(APPLICATION_JSON_VALUE)
              .body(bookingDto1)
              .when()
              .put(BASE_PATH + "/{uuid}")
              .as(ApiErrorDto.class);
      // then
      assertThat(result.getStatus()).isEqualTo(BAD_REQUEST.value());
      String message =
          format(
              "No vacant dates available from %s to %s",
              bookingDto1.getStartDate(), bookingDto1.getEndDate());
      assertThat(result.getMessage()).isEqualTo(message);
    }

    @Test
    void given_existing_booking_was_updated_by_another_transaction__then_status_conflict() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      BookingDto bookingDto =
          nextBookingDto().toBuilder()
              .uuid(bookingEntity.getUuid())
              .version(bookingEntity.getVersion())
              .campsiteId(bookingEntity.getCampsiteId())
              .startDate(bookingEntity.getStartDate())
              .endDate(bookingEntity.getEndDate().plusDays(2))
              .active(bookingEntity.isActive())
              .build();
      testDataHelper.updateBookingEntity(
          bookingEntity.toBuilder().endDate(bookingEntity.getEndDate().plusDays(5)).build());
      // when
      ApiErrorDto result =
          given()
              .pathParam("uuid", bookingDto.getUuid())
              .contentType(APPLICATION_JSON_VALUE)
              .body(bookingDto)
              .when()
              .put(BASE_PATH + "/{uuid}")
              .as(ApiErrorDto.class);
      // then
      assertThat(result.getStatus()).isEqualTo(CONFLICT.value());
      var message =
          format(
              "Optimistic locking error: %s with id %s was updated by another transaction",
              BookingEntity.class.getCanonicalName(), bookingEntity.getId());
      assertThat(result.getMessage()).isEqualTo(message);
    }
  }

  @Nested
  class CancelBooking {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      BookingEntity bookingEntity = testDataHelper.createBookingEntity(campsiteEntity.getId());
      // when
      given()
          .pathParam("uuid", bookingEntity.getUuid())
          .when()
          .delete(BASE_PATH + "/{uuid}")
          .then()
          .statusCode(OK.value());
    }
  }
}
