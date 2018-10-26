package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.model.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

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
  @Query("SELECT b FROM Booking b "
      + "WHERE b.endDate > ?1 AND b.endDate <= ?2 "
      + "ORDER BY b.startDate ASC")
  List<Booking> findForDateRange(LocalDate startDateRange, LocalDate endDateRange);
}
