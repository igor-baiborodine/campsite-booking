package com.kiroule.campsitebooking.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Model domain object representing a campsite.
 *
 * @author Igor Baiborodine
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Campsite extends DateAudit {

  /** Persistence ID */
  private Long id;

  /** Capacity, max number of people that campsite can accommodate */
  private Integer capacity;

  private boolean restrooms;

  private boolean drinkingWater;

  private boolean picnicTable;

  private boolean firePit;

  private boolean active;
}
