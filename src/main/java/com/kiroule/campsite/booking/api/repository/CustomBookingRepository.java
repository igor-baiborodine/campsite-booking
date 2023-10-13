package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;

public interface CustomBookingRepository {

  String FIND_FOR_DATE_RANGE_QUERY =
      "select b from BookingEntity b "
          + "where ((b.startDate < ?1 and ?2 < b.endDate) "
          + "or (?1 < b.endDate and b.endDate <= ?2) "
          + "or (?1 <= b.startDate and b.startDate <=?2)) "
          + "and b.active = true "
          + "and b.campsiteId = ?3 ";

  /**
   * Find active bookings for the given date range and campsite ID with pessimistic write locking.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @param campsiteId campsite ID
   * @return list of active bookings for the given date range and campsite ID
   */
  List<BookingEntity> findForDateRangeWithPessimisticWriteLocking(
      LocalDate startDate, LocalDate endDate, Long campsiteId);
}
