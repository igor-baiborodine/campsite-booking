package com.kiroule.campsite.booking.api.repository.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscores;
import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link MysqlCustomRepositoryContextImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CustomReplaceUnderscores.class)
class MysqlCustomRepositoryContextImplTest {

  @Mock
  NativeQuery query;

  @Mock
  EntityManager entityManager;

  @InjectMocks
  MysqlCustomRepositoryContextImpl customRepositoryContext;

  Long timeout;
  Integer count;

  @BeforeEach
  void beforeEach() {
    timeout = null;
    count = null;
  }

  @Test
  void set_lock_timeout__happy_path() {
    given_timeout(3000L);
    given_entityManagerCreatesNativeQuery();
    given_queryExecutesUpdate(1);

    when_setLockTimeout();

    then_assertUpdateExecuted(1);
  }

  @Test
  void get_lock_timeout__happy_path() {
    given_entityManagerCreatesNativeQuery();
    given_queryReturnsSingleResult(3L); // seconds

    when_getLockTimeout();

    then_assertFetchedTimeout(3000L); // milliseconds
  }

  private void given_timeout(long timeout) {
    this.timeout = timeout;
  }

  private void given_entityManagerCreatesNativeQuery() {
    doReturn(query).when(entityManager).createNativeQuery(any());
  }

  private void given_queryExecutesUpdate(int updatedCount) {
    doReturn(updatedCount).when(query).executeUpdate();
  }

  private void given_queryReturnsSingleResult(long timeoutInSec) {
    doReturn(timeoutInSec).when(query).getSingleResult();
  }

  private void when_setLockTimeout() {
    count = customRepositoryContext.setLockTimeout(timeout);
  }

  private void when_getLockTimeout() {
    timeout = customRepositoryContext.getLockTimeout();
  }

  private void then_assertUpdateExecuted(int expectedCount) {
    assertThat(count).isEqualTo(expectedCount);
    verify(entityManager).createNativeQuery("set session innodb_lock_wait_timeout = 3");
    verify(query).executeUpdate();
  }

  private void then_assertFetchedTimeout(long expectedTimeout) {
    assertThat(timeout).isEqualTo(expectedTimeout);
    verify(entityManager).createNativeQuery("select @@innodb_lock_wait_timeout");
    verify(query).getSingleResult();
  }

}