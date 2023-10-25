package com.kiroule.campsitebooking.repository.context;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlCustomRepositoryContextImpl extends CustomRepositoryContext {

  public MysqlCustomRepositoryContextImpl(EntityManager entityManager) {
    super(entityManager);
  }

  public int setLockTimeout(long timeoutDurationInMs) {
    String query =
        "set session innodb_lock_wait_timeout = " + MILLISECONDS.toSeconds(timeoutDurationInMs);
    return getEntityManager().createNativeQuery(query).executeUpdate();
  }

  public long getLockTimeout() {
    Query query = getEntityManager().createNativeQuery("select @@innodb_lock_wait_timeout");
    return SECONDS.toMillis((long) query.getSingleResult());
  }
}
