package com.kiroule.campsite.booking.api.repository;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.kiroule.campsite.booking.api.config.QueryProperties;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.context.CustomRepositoryContext;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomBookingRepositoryImpl implements CustomBookingRepository {

  private final CustomRepositoryContext customRepositoryContext;
  private final QueryProperties queryProperties;

  @Override
  public List<Booking> findForDateRangeWithPessimisticWriteLocking(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {

    log.info("Lock timeout before executing query[{}]", customRepositoryContext.getLockTimeout());

    Query query = customRepositoryContext.getEntityManager()
        .createQuery(FIND_FOR_DATE_RANGE)
        .setParameter(1, startDate)
        .setParameter(2, endDate)
        .setParameter(3, campsiteId)
        .setLockMode(PESSIMISTIC_WRITE);

    customRepositoryContext.setLockTimeout(
        queryProperties.getFindForDateRangeWithPessimisticWriteLockingLockTimeoutInMs());

    List<Booking> bookings = query.getResultList();
    log.info("Lock timeout after executing query[{}]", customRepositoryContext.getLockTimeout());

    return bookings;
  }
}