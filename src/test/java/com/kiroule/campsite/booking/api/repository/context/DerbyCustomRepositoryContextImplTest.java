package com.kiroule.campsite.booking.api.repository.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import jakarta.persistence.EntityManager;
import org.hibernate.query.NativeQuery;
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
class DerbyCustomRepositoryContextImplTest {

  @Mock NativeQuery<?> query;

  @Mock EntityManager entityManager;

  @InjectMocks DerbyCustomRepositoryContextImpl classUnderTest;

  @Nested
  class SetLockTimeout {

    @Test
    void happy_path() {
      // given
      long timeout = 3000L;
      doReturn(query).when(entityManager).createNativeQuery(any());
      doReturn(1).when(query).executeUpdate();
      // when
      int result = classUnderTest.setLockTimeout(timeout);
      // then
      assertThat(result).isEqualTo(1);
      verify(entityManager)
          .createNativeQuery(
              "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.locks.waitTimeout',  '3')");
      verify(query).executeUpdate();
    }
  }

  @Nested
  class GetLockTimeout {

    @Test
    void happy_path() {
      // given
      doReturn(query).when(entityManager).createNativeQuery(any());
      doReturn("3").when(query).getSingleResult();
      // when
      long result = classUnderTest.getLockTimeout();
      // then
      assertThat(result).isEqualTo(3000L);
      verify(entityManager)
          .createNativeQuery(
              "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.locks.waitTimeout')");
      verify(query).getSingleResult();
    }
  }
}
