package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.model.Booking;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Booking} instances.
 *
 * @author Igor Baiborodine
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {

  /**
   * Find a booking for the given UUID.
   *
   * @param uuid booking uuid
   * @return booking for the given UUID
   */
  Optional<Booking> findByUuid(UUID uuid);

  /**
   * Find active bookings for the given date range.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @return list of active bookings for the given date range
   */
  @Lock(LockModeType.PESSIMISTIC_READ)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="100")})
  @Query("select b from Booking b "
      + "where ((b.startDate < ?1 and ?2 < b.endDate) "
      + "or (?1 < b.endDate and b.endDate <= ?2) "
      + "or (?1 <= b.startDate and b.startDate <=?2)) "
      + "and b.active = true "
      + "order by b.startDate asc")
  List<Booking> findForDateRange(LocalDate startDate, LocalDate endDate);
}
