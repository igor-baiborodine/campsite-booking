package com.kiroule.campsite.booking.api.contract.v2.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiroule.campsite.booking.api.model.validator.BookingAllowedStartDate;
import com.kiroule.campsite.booking.api.model.validator.BookingMaximumStay;
import com.kiroule.campsite.booking.api.model.validator.BookingStartDateBeforeEndDate;
import java.time.LocalDate;
import java.util.UUID;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Builder
@Data
@AllArgsConstructor
@BookingStartDateBeforeEndDate
@BookingAllowedStartDate
@BookingMaximumStay
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
public class BookingDto {

  /**
   * Persistence ID
   */
  private Long id;

  /**
   * Business ID
   */
  @NotNull
  @EqualsAndHashCode.Include
  private UUID uuid;

  private Long version;

  @NotNull
  private Long campsiteId;

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
