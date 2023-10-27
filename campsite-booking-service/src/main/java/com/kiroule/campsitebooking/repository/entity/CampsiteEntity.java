package com.kiroule.campsitebooking.repository.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entity domain object representing a campsite.
 *
 * @author Igor Baiborodine
 */
@Entity()
@Table(name = "campsites")
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CampsiteEntity extends DateAuditEntity {

  /** Persistence ID */
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  /** Capacity, max number of people that campsite can accommodate */
  @Column(name = "capacity", nullable = false)
  private Integer capacity;

  @Column(name = "restrooms", nullable = false)
  private boolean restrooms;

  @Column(name = "drinking_water", nullable = false)
  private boolean drinkingWater;

  @Column(name = "picnic_table", nullable = false)
  private boolean picnicTable;

  @Column(name = "fire_pit", nullable = false)
  private boolean firePit;

  @Column(name = "active", nullable = false)
  private boolean active;
}
