package com.kiroule.campsitebooking;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiroule.campsitebooking.repository.BookingRepository;
import com.kiroule.campsitebooking.repository.CampsiteRepository;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

@org.junit.jupiter.api.parallel.Execution(org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD)
class FrameworkPilotIT extends BaseIT {
  @Autowired CampsiteRepository campsites;
  @Autowired BookingRepository bookings;
  @Autowired ObjectMapper mapper;
  @LocalServerPort int port;

  private long campsite() {
    return campsites
        .saveAndFlush(CampsiteEntity.builder().capacity(4).active(true).build())
        .getId();
  }

  private String booking(long site, LocalDate day, int nights, String name) throws Exception {
    return mapper.writeValueAsString(
        mapper
            .createObjectNode()
            .put("campsiteId", site)
            .put("email", name + "@example.invalid")
            .put("fullName", name)
            .put("startDate", day.plusDays(1).toString())
            .put("endDate", day.plusDays(1 + nights).toString())
            .put("active", true));
  }

  private HttpResponse<String> request(String method, String path, String body) throws Exception {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()
        .send(
            HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + port + "/api/v2/booking" + path))
                .timeout(Duration.ofSeconds(45))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void given_valid_booking__then_create_read_availability_and_version_preserved() throws Exception {
    // given
    LocalDate day = LocalDate.now();
    long site = campsite();
    String input = booking(site, day, 1, "Pilot Baseline");
    // when
    var result = request("POST", "", input);
    // then
    assertThat(result.statusCode()).isEqualTo(201);
    JsonNode created = mapper.readTree(result.body());
    String uuid = created.get("uuid").asText();
    assertThat(result.headers().firstValue("Location").orElseThrow()).endsWith(uuid);
    var read = request("GET", "/" + uuid, "");
    assertThat(read.statusCode()).isEqualTo(200);
    JsonNode actual = mapper.readTree(read.body());
    mapper
        .readTree(input)
        .fields()
        .forEachRemaining(e -> assertThat(actual.get(e.getKey())).isEqualTo(e.getValue()));
    assertThat(actual.get("version").asLong()).isZero();
    var overlap = request("POST", "", input);
    assertThat(overlap.statusCode()).isEqualTo(400);
    assertThat(mapper.readTree(overlap.body()).get("message").asText())
        .isEqualTo("No vacant dates available from " + day.plusDays(1) + " to " + day.plusDays(2));
    var stored = bookings.findByUuid(UUID.fromString(uuid)).orElseThrow();
    assertThat(stored.getEmail()).isEqualTo("Pilot Baseline@example.invalid");
    assertThat(stored.getStartDate()).isEqualTo(day.plusDays(1));
    assertThat(stored.getEndDate()).isEqualTo(day.plusDays(2));
    var vacant =
        request(
            "GET",
            "/vacant-dates?campsite_id="
                + site
                + "&start_date="
                + day.plusDays(1)
                + "&end_date="
                + day.plusDays(3),
            "");
    assertThat(vacant.statusCode()).isEqualTo(200);
    assertThat(mapper.readTree(vacant.body()))
        .isEqualTo(mapper.readTree("[\"" + day.plusDays(2) + "\",\"" + day.plusDays(3) + "\"]"));
    assertThat(request("PUT", "/" + uuid, read.body()).statusCode()).isEqualTo(200);
    var stale = request("PUT", "/" + uuid, read.body());
    assertThat(stale.statusCode()).isEqualTo(409);
    assertThat(mapper.readTree(stale.body()).get("message").asText())
        .startsWith("Optimistic locking error:");
    assertThat(bookings.findByUuid(UUID.fromString(uuid)).orElseThrow().getVersion()).isEqualTo(1L);
    assertThat(LocalDate.now()).isEqualTo(day);
  }

  @Test
  void given_invalid_requests__then_errors_preserved() throws Exception {
    // given
    LocalDate day = LocalDate.now();
    long site = campsite();
    // when
    var result = request("POST", "", booking(site, day, 4, "Pilot Invalid"));
    // then
    assertThat(result.statusCode()).isEqualTo(400);
    var error = mapper.readTree(result.body());
    assertThat(error.get("status").asInt()).isEqualTo(400);
    assertThat(error.get("message").asText())
        .isEqualTo(
            "createBooking.booking: Booking stay length must be less or equal to three days");
    assertThat(error.get("subErrors").size()).isZero();
    var malformed = request("POST", "", "{");
    assertThat(malformed.statusCode()).isEqualTo(400);
    assertThat(mapper.readTree(malformed.body()).get("message").asText())
        .isEqualTo("Malformed JSON request");
    assertThat(request("GET", "/00000000-0000-0000-0000-000000000005", "").statusCode())
        .isEqualTo(404);
    var validation =
        request(
            "POST",
            "",
            booking(site, day, 1, "Pilot Invalid")
                .replace("Pilot Invalid@example.invalid", "x".repeat(51)));
    assertThat(validation.statusCode()).isEqualTo(400);
    assertThat(mapper.readTree(validation.body()).get("message").asText())
        .isEqualTo("Validation error");
    assertThat(mapper.readTree(validation.body()).get("subErrors").size()).isPositive();
    assertThat(LocalDate.now()).isEqualTo(day);
  }

  @Test
  void given_three_simultaneous_bookings__then_exactly_one_stored() throws Exception {
    // given
    LocalDate day = LocalDate.now();
    long site = campsite();
    var barrier = new CyclicBarrier(3);
    var executor = Executors.newFixedThreadPool(3);
    List<Future<HttpResponse<String>>> futures = new ArrayList<>();
    try {
      for (int i = 0; i < 3; i++) {
        String input = booking(site, day, 1, "Pilot Concurrent " + i);
        futures.add(
            executor.submit(
                () -> {
                  barrier.await(10, TimeUnit.SECONDS);
                  return request("POST", "", input);
                }));
      }
      // when
      List<HttpResponse<String>> result = new ArrayList<>();
      for (var future : futures) result.add(future.get(60, TimeUnit.SECONDS));
      // then
      assertThat(result.stream().map(HttpResponse::statusCode).toList())
          .containsExactlyInAnyOrder(201, 400, 400);
      var rows = bookings.findForDateRange(day.plusDays(1), day.plusDays(2), site);
      assertThat(rows).hasSize(1);
      var winner =
          mapper.readTree(
              result.stream().filter(r -> r.statusCode() == 201).findFirst().orElseThrow().body());
      assertThat(rows.get(0).getUuid().toString()).isEqualTo(winner.get("uuid").asText());
      assertThat(rows.get(0).getEmail()).isEqualTo(winner.get("email").asText());
      assertThat(rows.get(0).getFullName())
          .isIn("Pilot Concurrent 0", "Pilot Concurrent 1", "Pilot Concurrent 2");
      assertThat(LocalDate.now()).isEqualTo(day);
    } finally {
      executor.shutdownNow();
      assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
    }
  }
}
