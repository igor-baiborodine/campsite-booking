package com.kiroule.campsite.booking.api.contract.v2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.kiroule.campsite.booking.api.model.validator.BookingAllowedStartDate;
import com.kiroule.campsite.booking.api.model.validator.BookingMaximumStay;
import com.kiroule.campsite.booking.api.model.validator.BookingStartDateBeforeEndDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

/**
 * DTO domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@BookingStartDateBeforeEndDate
@BookingAllowedStartDate
@BookingMaximumStay
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated
public class BookingDto {

  /** Business ID */
  private UUID uuid;

  private Long version;

  @NotNull private Long campsiteId;

  @NotEmpty
  @Size(max = 50)
  private String email;

  @NotEmpty
  @Size(max = 50)
  private String fullName;

  @NotNull
  @Future(message = "Booking start date must be a future date")
  @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate startDate;

  @NotNull
  @Future(message = "Booking end date must be a future date")
  @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate endDate;

  private boolean active;
}
