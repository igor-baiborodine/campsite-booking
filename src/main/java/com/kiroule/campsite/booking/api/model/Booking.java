package com.kiroule.campsite.booking.api.model;

import static java.sql.Types.VARCHAR;
import static java.util.Objects.isNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Entity domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Entity
@Table(name = "bookings")
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Generated
public class Booking extends DateAudit {

  /** Persistence ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Business ID */
  @Getter
  @EqualsAndHashCode.Include
  @Column(name = "uuid", nullable = false, unique = true)
  @JdbcTypeCode(VARCHAR)
  private UUID uuid;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campsite_id")
  private Campsite campsite;

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
  public List<LocalDate> getBookingDatesWithEndDateExclusive() {
    return startDate.datesUntil(endDate).toList();
  }

  public Long getCampsiteId() {
    return Optional.of(campsite).orElseThrow().getId();
  }
}
