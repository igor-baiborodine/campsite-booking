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

  String FIND_FOR_DATE_RANGE = "select b from Booking b "
      + "where ((b.startDate < ?1 and ?2 < b.endDate) "
      + "or (?1 < b.endDate and b.endDate <= ?2) "
      + "or (?1 <= b.startDate and b.startDate <=?2)) "
      + "and b.active = true "
      + "and b.campsite.id = ?3 ";

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

  /**
   * Find active bookings for the given date range and campsite ID with pessimistic write locking.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @param campsiteId campsite ID
   * @return list of active bookings for the given date range and campsite ID
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value ="100")})
  @Query(FIND_FOR_DATE_RANGE)
  List<Booking> findForDateRangeWithPessimisticWriteLocking(LocalDate startDate, LocalDate endDate, Long campsiteId);

}
