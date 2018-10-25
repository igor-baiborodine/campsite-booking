package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for {@link Booking} instances.
 *
 * @author Igor Baiborodine
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {

  /**
   * Find bookings for the given date range.
   *
   * @param startDateRange start date range
   * @param endDateRange end date range
   * @return list of bookings for the given date range
   */
  @Query(
      "select b from bookings b where b.end_date > ?1 and b.end_date <= ?2 order by b.start_date asc")
  List<Booking> findForGivenDateRange(Date startDateRange, Date endDateRange);
}
