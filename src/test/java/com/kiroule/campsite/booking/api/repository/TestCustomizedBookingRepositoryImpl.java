package com.kiroule.campsite.booking.api.repository;

import static java.lang.Thread.currentThread;

import com.kiroule.campsite.booking.api.config.QueryProperties;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.repository.context.CustomizedRepositoryContext;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestCustomizedBookingRepositoryImpl extends CustomizedBookingRepositoryImpl {

  public TestCustomizedBookingRepositoryImpl(
      CustomizedRepositoryContext customizedRepositoryContext, QueryProperties queryProperties) {
    super(customizedRepositoryContext, queryProperties);
  }

  @Override
  public List<Booking> findForDateRangeWithPessimisticWriteLocking(
      LocalDate startDate, LocalDate endDate, Long campsiteId) {
    log.info("Obtaining pessimistic locking, thread[{}] ...", currentThread());

    List<Booking> bookings = super.findForDateRangeWithPessimisticWriteLocking(startDate, endDate, campsiteId);

    log.info("Delay releasing pessimistic lock, thread[{}] ...", currentThread());
    delayReleasingPessimisticLock();
    log.info("Pessimistic lock released, thread[{}].", currentThread());

    return bookings;
  }

  void delayReleasingPessimisticLock() {
    // mock in test
  }

}
