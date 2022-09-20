package com.kiroule.campsite.booking.api.repository.context;

import javax.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class CustomRepositoryContext {

  private final EntityManager entityManager;

  public abstract int setLockTimeout(long timeoutDurationInMs);

  public abstract long getLockTimeout();

}
