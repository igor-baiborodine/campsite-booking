package com.kiroule.campsite.booking.api.repository.context;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class MysqlCustomRepositoryContextImpl extends CustomRepositoryContext {

  public MysqlCustomRepositoryContextImpl(EntityManager entityManager) {
    super(entityManager);
  }

  public int setLockTimeout(long timeoutDurationInMs) {
    long timeoutDurationInSec = TimeUnit.MILLISECONDS.toSeconds(timeoutDurationInMs);
    Query query = getEntityManager().createNativeQuery(
        "set session innodb_lock_wait_timeout = " + timeoutDurationInSec);
    return query.executeUpdate();
  }

  public long getLockTimeout() {
    Query query = getEntityManager().createNativeQuery("select @@innodb_lock_wait_timeout");
    long timeoutDurationInSec = ((BigInteger) query.getSingleResult()).longValue();
    return TimeUnit.SECONDS.toMillis(timeoutDurationInSec);
  }

}
