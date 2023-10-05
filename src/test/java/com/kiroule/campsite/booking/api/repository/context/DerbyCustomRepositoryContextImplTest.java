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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link DerbyCustomRepositoryContextImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CustomReplaceUnderscores.class)
class DerbyCustomRepositoryContextImplTest {

  @Mock
  NativeQuery query;

  @Mock
  EntityManager entityManager;

  @InjectMocks
  DerbyCustomRepositoryContextImpl classUnderTest;

  Long timeout;
  Integer count;

  @BeforeEach
  void beforeEach() {
    timeout = null;
    count = null;
  }

  @Nested
  class SetLockTimeout {

    @Test
    void happy_path() {
      given_timeout(3000L);
      given_entityManagerCreatesNativeQuery();
      given_queryExecutesUpdate(1);

      when_setLockTimeout();

      then_assertUpdateExecuted(1);
    }

    private void given_timeout(long value) {
      timeout = value;
    }

    private void given_queryExecutesUpdate(int updatedCount) {
      doReturn(updatedCount).when(query).executeUpdate();
    }

    private void when_setLockTimeout() {
      count = classUnderTest.setLockTimeout(timeout);
    }

    private void then_assertUpdateExecuted(int expectedCount) {
      assertThat(count).isEqualTo(expectedCount);
      verify(entityManager).createNativeQuery("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout',  '3')");
      verify(query).executeUpdate();
    }
  }

  @Nested
  class GetLockTimeout {

    @Test
    void happy_path() {
      given_entityManagerCreatesNativeQuery();
      given_queryReturnsSingleResult("3");

      when_getLockTimeout();

      then_assertFetchedTimeout(3000L);
    }

    private void given_queryReturnsSingleResult(String timeoutInSec) {
      doReturn(timeoutInSec).when(query).getSingleResult();
    }

    private void when_getLockTimeout() {
      timeout = classUnderTest.getLockTimeout();
    }

    private void then_assertFetchedTimeout(long expectedTimeout) {
      assertThat(timeout).isEqualTo(expectedTimeout);
      verify(entityManager).createNativeQuery("VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.locks.waitTimeout')");
      verify(query).getSingleResult();
    }
  }

  private void given_entityManagerCreatesNativeQuery() {
    doReturn(query).when(entityManager).createNativeQuery(any());
  }
}