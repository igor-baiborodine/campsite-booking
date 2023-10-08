package com.kiroule.campsite.booking.api.model;

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
@Generated
public class Campsite extends DateAudit {

  /** Persistence ID */
  private Long id;

  /** Capacity, max number of people that campsite can accommodate */
  private int capacity;

  private boolean restrooms;

  private boolean drinkingWater;

  private boolean picnicTable;

  private boolean firePit;

  private boolean active;
}
