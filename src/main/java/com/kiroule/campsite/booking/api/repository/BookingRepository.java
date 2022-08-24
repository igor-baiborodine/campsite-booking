package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for {@link Booking} instances.
 *
 * @author Igor Baiborodine
 */
public interface BookingRepository extends CrudRepository<Booking, Long>, CustomizedBookingRepository {

  /**
   * Find a booking for the given UUID.
   *
   * @param uuid booking uuid
   * @return booking for the given UUID
   */
  Optional<Booking> findByUuid(UUID uuid);

  /**
   * Find active bookings for the given date range and campsite ID with pessimistic write locking.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @param campsiteId campsite ID
   * @return list of active bookings for the given date range and campsite ID
   */
  @Query(FIND_FOR_DATE_RANGE)
  List<Booking> findForDateRange(LocalDate startDate, LocalDate endDate, Long campsiteId);

}
