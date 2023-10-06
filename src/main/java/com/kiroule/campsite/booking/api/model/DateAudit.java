package com.kiroule.campsite.booking.api.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.Generated;

@Data
@Generated
public abstract class DateAudit {

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
