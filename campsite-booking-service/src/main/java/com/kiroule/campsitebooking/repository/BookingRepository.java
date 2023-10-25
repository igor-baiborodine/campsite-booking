package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.repository.entity.BookingEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for {@link BookingEntity} instances.
 *
 * @author Igor Baiborodine
 */
public interface BookingRepository
    extends JpaRepository<BookingEntity, Long>, CustomBookingRepository {

  /**
   * Find a booking for the given UUID.
   *
   * @param uuid booking uuid
   * @return booking for the given UUID
   */
  Optional<BookingEntity> findByUuid(UUID uuid);

  /**
   * Find active bookings for the given date range and campsite ID with pessimistic write locking.
   *
   * @param startDate range start date
   * @param endDate range end date
   * @param campsiteId campsite ID
   * @return list of active bookings for the given date range and campsite ID
   */
  @Query(FIND_FOR_DATE_RANGE_QUERY)
  List<BookingEntity> findForDateRange(LocalDate startDate, LocalDate endDate, Long campsiteId);
}
