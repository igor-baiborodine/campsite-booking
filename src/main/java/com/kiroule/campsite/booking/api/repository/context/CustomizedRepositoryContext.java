package com.kiroule.campsite.booking.api.repository.context;

import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class CustomizedRepositoryContext {

  private static final String[] TIME_MEASURES = {"ms", "s", "min", "h", "d"};
  private static final TimeUnit[] TIME_UNITS = {
    TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS
  };

  private final EntityManager entityManager;

  public abstract int setLockTimeout(long timeoutDurationInMs);

  public abstract long getLockTimeout();

  protected long parseLockTimeOutToMilliseconds(String lockTimeOut) {
    for (int idx = 0; idx < TIME_MEASURES.length; idx++) {
      if (lockTimeOut.contains(TIME_MEASURES[idx])) {
        return Long.valueOf(
                lockTimeOut.substring(0, lockTimeOut.length() - TIME_MEASURES[idx].length()))
            * TIME_UNITS[idx].toMillis(1);
      }
    }

    return Long.valueOf(lockTimeOut);
  }
}
