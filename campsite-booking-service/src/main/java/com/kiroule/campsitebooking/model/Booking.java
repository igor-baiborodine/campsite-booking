package com.kiroule.campsitebooking.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.kiroule.campsitebooking.model.validator.BookingAllowedStartDate;
import com.kiroule.campsitebooking.model.validator.BookingMaximumStay;
import com.kiroule.campsitebooking.model.validator.BookingStartDateBeforeEndDate;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@BookingStartDateBeforeEndDate
@BookingAllowedStartDate
@BookingMaximumStay
public class Booking extends DateAudit {

  /** Persistence ID */
  private Long id;

  /** Business ID */
  private UUID uuid;

  private Long version;

  private Long campsiteId;

  private String email;

  private String fullName;

  private LocalDate startDate;

  private LocalDate endDate;

  private boolean active;

  /**
   * Returns a list of booking dates. The returned list starts from the booking start date
   * (inclusive) and goes to the booking end date (exclusive).
   *
   * @return a list of booking dates
   */
  public List<LocalDate> getBookingDatesWithEndDateExclusive() {
    return startDate.datesUntil(endDate).toList();
  }
}
