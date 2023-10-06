package com.kiroule.campsite.booking.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

/**
 * Model domain object representing a campsite.
 *
 * @author Igor Baiborodine
 */
@Data
@Builder(toBuilder = true)
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
