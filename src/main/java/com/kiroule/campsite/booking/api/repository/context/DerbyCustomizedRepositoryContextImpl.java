package com.kiroule.campsite.booking.api.repository.context;

import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DerbyCustomizedRepositoryContextImpl extends CustomizedRepositoryContext {

  public DerbyCustomizedRepositoryContextImpl(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public int setLockTimeout(long timeoutDurationInMs) {
    long timeoutDurationInSec = TimeUnit.MILLISECONDS.toSeconds(timeoutDurationInMs);
    Query query = getEntityManager().createNativeQuery(String.format(
        "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout',  '%d')", timeoutDurationInSec));
    return query.executeUpdate();
  }

  @Override
  public long getLockTimeout() {
    Query query = getEntityManager().createNativeQuery(
        "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.locks.waitTimeout')");
    long timeoutDurationInSec = Long.parseLong((String) query.getSingleResult());
    return TimeUnit.SECONDS.toMillis(timeoutDurationInSec);
  }

}
