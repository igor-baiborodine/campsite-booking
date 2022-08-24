package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.model.Booking;
import java.time.LocalDate;
import java.util.List;

public interface CustomizedBookingRepository {

  String FIND_FOR_DATE_RANGE = "select b from Booking b "
      + "where ((b.startDate < ?1 and ?2 < b.endDate) "
      + "or (?1 < b.endDate and b.endDate <= ?2) "
      + "or (?1 <= b.startDate and b.startDate <=?2)) "
      + "and b.active = true "
      + "and b.campsite.id = ?3 ";

  /**
   * Find active bookings for the given date range and campsite ID with pessimistic write locking.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @param campsiteId campsite ID
   * @return list of active bookings for the given date range and campsite ID
   */
  List<Booking> findForDateRangeWithPessimisticWriteLocking(LocalDate startDate, LocalDate endDate, Long campsiteId);

}
