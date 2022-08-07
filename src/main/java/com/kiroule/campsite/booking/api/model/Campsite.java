package com.kiroule.campsite.booking.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Entity domain object representing a campsite.
 *
 * @author Igor Baiborodine
 */
@Entity
@Table(name = "campsites")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Generated
public class Campsite extends DateAudit {

  /**
   * Persistence ID
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  /**
   * Capacity, max number of people that campsite can accommodate
   */
  @Column(name = "capacity", nullable = false)
  private int capacity;

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
