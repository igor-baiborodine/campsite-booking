package com.kiroule.campsite.booking.api.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Generated;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@Generated
public abstract class DateAudit {

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
