package com.kiroule.campsitebooking.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.kiroule.campsitebooking.config.QueryProperties;
import com.kiroule.campsitebooking.repository.entity.BookingEntity;
import com.kiroule.campsitebooking.repository.context.CustomRepositoryContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CustomBookingRepositoryImpl implements CustomBookingRepository {

  private final CustomRepositoryContext customRepositoryContext;
  private final QueryProperties queryProperties;

  @Override
  public List<BookingEntity> findForDateRangeWithPessimisticWriteLocking(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {

    log.info("Lock timeout before executing query[{}]", customRepositoryContext.getLockTimeout());

    Query query =
        customRepositoryContext
            .getEntityManager()
            .createQuery(FIND_FOR_DATE_RANGE_QUERY)
            .setParameter(1, startDate)
            .setParameter(2, endDate)
            .setParameter(3, campsiteId)
            .setLockMode(PESSIMISTIC_WRITE);

    customRepositoryContext.setLockTimeout(
        queryProperties.getFindForDateRangeWithPessimisticWriteLockingLockTimeoutInMs());

    List<BookingEntity> bookings = query.getResultList();
    log.info("Lock timeout after executing query[{}]", customRepositoryContext.getLockTimeout());

    return bookings;
  }
}
