package com.kiroule.campsitebooking.model;

import java.time.Instant;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
public abstract class DateAudit {

  private Instant createdAt;

  private Instant updatedAt;
}
