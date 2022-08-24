package com.kiroule.campsite.booking.api.repository.context;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class PostgreSqlCustomizedRepositoryContextImpl extends CustomizedRepositoryContext {

  public PostgreSqlCustomizedRepositoryContextImpl(EntityManager entityManager) {
    super(entityManager);
  }

  public void setLockTimeout(long timeoutDurationInMs) {
    Query query = getEntityManager().createNativeQuery("set local lock_timeout = " + timeoutDurationInMs);
    query.executeUpdate();
  }

  public long getLockTimeout() {
    Query query = getEntityManager().createNativeQuery("show lock_timeout");
    String result = (String) query.getSingleResult();
    return parseLockTimeOutToMilliseconds(result);
  }

}
