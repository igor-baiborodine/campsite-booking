package com.kiroule.campsite.booking.api.model;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;

/**
 * Model domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Data
@Builder(toBuilder = true)
@Generated
public class Booking extends DateAudit {

  /** Persistence ID */
  private Long id;

  /** Business ID */
  private UUID uuid;

  private Long version;

  private Campsite campsite;

  private String email;

  private String fullName;

  private LocalDate startDate;

  private LocalDate endDate;

  private boolean active;

  /**
   * Checks if the booking is new.
   *
   * @return true if the booking is new, i.e., there is no persistence ID, otherwise false
   */
  public boolean isNew() {
    return isNull(id);
  }

  /**
   * Returns a list of booking dates. The returned list starts from the booking start date
   * (inclusive) and goes to the booking end date (exclusive).
   *
   * @return a list of booking dates
   */
  public List<LocalDate> getBookingDatesWithEndDateExclusive() {
    return startDate.datesUntil(endDate).toList();
  }

  /**
   * Returns the campsite ID for which a booking is made for.
   *
   * @return a campsite ID
   */
  public Long getCampsiteId() {
    return Optional.of(campsite).orElseThrow().getId();
  }
}
