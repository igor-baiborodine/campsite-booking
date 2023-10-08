package com.kiroule.campsite.booking.api.contract.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiroule.campsite.booking.api.model.validator.BookingAllowedStartDate;
import com.kiroule.campsite.booking.api.model.validator.BookingMaximumStay;
import com.kiroule.campsite.booking.api.model.validator.BookingStartDateBeforeEndDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;

/**
 * DTO domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@BookingStartDateBeforeEndDate
@BookingAllowedStartDate
@BookingMaximumStay
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
public class BookingDto {

  /** Business ID */
  @NotNull @EqualsAndHashCode.Include private UUID uuid;

  @NotNull private Long campsiteId;

  @NotEmpty
  @Size(max = 50)
  private String email;

  @NotEmpty
  @Size(max = 50)
  private String fullName;

  @NotNull
  @Future(message = "Booking start date must be a future date")
  private LocalDate startDate;

  @NotNull
  @Future(message = "Booking end date must be a future date")
  private LocalDate endDate;

  private boolean active;
}