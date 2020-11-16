package com.kiroule.campsitebooking.api.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiroule.campsitebooking.api.model.validator.BookingAllowedStartDate;
import com.kiroule.campsitebooking.api.model.validator.BookingMaximumStay;
import com.kiroule.campsitebooking.api.model.validator.BookingStartDateBeforeEndDate;
import java.time.LocalDate;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Version;
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

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  /**
   * Business ID
   */
  @EqualsAndHashCode.Include
  @Column(name = "uuid", nullable = false, unique = true)
  private UUID uuid;

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
