package com.kiroule.campsite.booking.api.config;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("query")
public class QueryProperties {

  @NotNull
  private Long findForDateRangeWithPessimisticWriteLockingLockTimeoutInMs;

}
