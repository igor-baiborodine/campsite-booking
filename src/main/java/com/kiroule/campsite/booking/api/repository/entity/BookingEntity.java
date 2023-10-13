package com.kiroule.campsite.booking.api.repository.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.sql.Types.VARCHAR;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Entity domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Entity()
@Table(name = "bookings")
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class BookingEntity extends DateAuditEntity {

  /** Persistence ID */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Business ID */
  @JdbcTypeCode(VARCHAR)
  @Column(name = "uuid", nullable = false, unique = true)
  private UUID uuid;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  @Column(name = "campsite_id", nullable = false)
  private Long campsiteId;

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
}
