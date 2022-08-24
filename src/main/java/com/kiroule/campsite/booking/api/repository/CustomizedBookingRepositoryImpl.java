package com.kiroule.campsite.booking.api.repository;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.kiroule.campsite.booking.api.config.QueryProperties;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.context.CustomizedRepositoryContext;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RequiredArgsConstructor
public class CustomizedBookingRepositoryImpl implements CustomizedBookingRepository {

  private final CustomizedRepositoryContext customizedRepositoryContext;
  private final QueryProperties queryProperties;

  @Override
  public List<Booking> findForDateRangeWithPessimisticWriteLocking(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {

    Query query = customizedRepositoryContext.getEntityManager().createQuery(FIND_FOR_DATE_RANGE);
    query.setParameter(1, startDate);
    query.setParameter(2, endDate);
    query.setParameter(3, campsiteId);
    query.setLockMode(PESSIMISTIC_WRITE);

    customizedRepositoryContext.setLockTimeout(
        queryProperties.getFindForDateRangeWithPessimisticWriteLockingLockTimeoutInMs());

    return query.getResultList();
  }
}
