package com.kiroule.campsite.booking.api.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Entity domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Booking extends DateAudit {

  /**
   * Persistence ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  /**
   * Business ID
   */
  @Getter
  @EqualsAndHashCode.Include
  @Column(name = "uuid", nullable = false, unique = true)
  private UUID uuid;

  @Column(name = "email", nullable = false, length = 50)
  private String email;

  @Column(name = "full_name", nullable = false, length = 50)
  private String fullName;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "active", nullable = false)
  private boolean active;

  /**
   * Checks if the booking is new.
   *
   * @return true if the booking is new, i.e., there is no persistence ID, otherwise false
   */
  public boolean isNew() {
    return this.id == null;
  }

  /**
   * Returns a list of booking dates. The returned list starts from the booking start date
   * (inclusive) and goes to the booking end date (exclusive).
   *
   * @return a list of booking dates
   */
  public List<LocalDate> getBookingDates() {
    return this.startDate.datesUntil(this.endDate).collect(Collectors.toList());
  }

}
