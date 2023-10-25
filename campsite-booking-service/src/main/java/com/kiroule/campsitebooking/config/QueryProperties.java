package com.kiroule.campsitebooking.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("query")
public class QueryProperties {

  @NotNull private Long findForDateRangeWithPessimisticWriteLockingLockTimeoutInMs;
}
