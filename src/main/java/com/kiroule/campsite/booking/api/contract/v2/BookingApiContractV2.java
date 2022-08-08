package com.kiroule.campsite.booking.api.contract.v2;

import com.kiroule.campsite.booking.api.contract.v2.model.BookingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Booking V2", description = "Booking API Contract V2")
@RequestMapping("/v2/booking")
public interface BookingApiContractV2 {

  @Operation(summary = "Get vacant dates within a given period", responses = {
      @ApiResponse(responseCode = "200", description = "Success")})
  @GetMapping(value = "/vacant-dates", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<List<LocalDate>> getVacantDates(
      @RequestParam(name = "start_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(name = "end_date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(name = "campsite_id") Long campsiteId);

  @Operation(summary = "Fetch booking for a given UUID", responses = {
      @ApiResponse(responseCode = "200", description = "Success"),
      @ApiResponse(responseCode = "404", description = "Not found: booking for a given UUID does not exist")})
  @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<EntityModel<BookingDto>> getBooking(@PathVariable() UUID uuid);

  @Operation(summary = "Add new booking", responses = {
      @ApiResponse(responseCode = "201", description = "Created: new booking was added"),
      @ApiResponse(responseCode = "400", description = "Bad request: new booking was not added")})
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<EntityModel<BookingDto>> addBooking(@RequestBody() @Valid BookingDto bookingDto);

  @Operation(summary = "Update existing booking", responses = {
      @ApiResponse(responseCode = "200", description = "Success: booking was updated"),
      @ApiResponse(responseCode = "400", description = "Bad request: existing booking was not updated"),
      @ApiResponse(responseCode = "404", description = "Not found: booking for a given UUID does not exist"),
      @ApiResponse(responseCode = "409", description = "Conflict: booking was updated by another transaction")})
  @PutMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<EntityModel<BookingDto>> updateBooking(@PathVariable("uuid") UUID uuid, @RequestBody @Valid BookingDto bookingDto);

  @Operation(summary = "Cancel existing booking", responses = {
      @ApiResponse(responseCode = "200", description = "Success: booking was cancelled"),
      @ApiResponse(responseCode = "400", description = "Bad request: existing booking was not updated"),
      @ApiResponse(responseCode = "404", description = "Not found: booking for a given UUID does not exist")})
  @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> cancelBooking (@PathVariable("uuid") UUID uuid);
}