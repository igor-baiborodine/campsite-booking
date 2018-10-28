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
  @Query("select b from Booking b "
      + "where (b.startDate < ?1 and ?2 < b.endDate) "
      + "or (?1 < b.endDate and b.endDate <= ?2) "
      + "or (?1 <= b.startDate and b.startDate <=?2) "
      + "order by b.startDate asc")
  List<Booking> findForDateRange(LocalDate startDateRange, LocalDate endDateRange);
}
