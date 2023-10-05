package com.kiroule.campsite.booking.api.repository.context;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class DerbyCustomRepositoryContextImpl extends CustomRepositoryContext {

  public DerbyCustomRepositoryContextImpl(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public int setLockTimeout(long timeoutDurationInMs) {
    String query =
        format(
            "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout',  '%d')",
            MILLISECONDS.toSeconds(timeoutDurationInMs));
    return getEntityManager().createNativeQuery(query).executeUpdate();
  }

  @Override
  public long getLockTimeout() {
    Query query =
        getEntityManager()
            .createNativeQuery(
                "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.locks.waitTimeout')");
    long timeoutDurationInSec =
        ofNullable((String) query.getSingleResult()).map(Long::parseLong).orElse(0L);
    return SECONDS.toMillis(timeoutDurationInSec);
  }
}
