package com.kiroule.campsite.booking.api.model;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import org.hibernate.annotations.Type;

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
  @Type(type="uuid-char")
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
    return isNull(id);
  }

  /**
   * Returns a list of booking dates. The returned list starts from the booking start date
   * (inclusive) and goes to the booking end date (exclusive).
   *
   * @return a list of booking dates
   */
  public List<LocalDate> getBookingDates() {
    return startDate.datesUntil(endDate).toList();
  }

}
