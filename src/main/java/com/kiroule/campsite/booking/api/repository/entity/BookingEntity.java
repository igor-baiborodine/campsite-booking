package com.kiroule.campsite.booking.api.repository.entity;

import static java.sql.Types.VARCHAR;

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
@Entity()
@Table(name = "bookings")
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Generated
public class BookingEntity extends DateAuditEntity {

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
  private CampsiteEntity campsite;

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
