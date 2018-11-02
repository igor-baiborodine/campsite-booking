package com.kiroule.campsitebooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Simple JavaBean domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

  /**
   * Holds value of property id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  /**
   * Holds value of property email.
   */
  @NotEmpty
  @Size(max = 50)
  @Column(name = "email", nullable = false, length = 50)
  private String email;

  /**
   * Holds value of property full name.
   */
  @NotEmpty
  @Size(max = 50)
  @Column(name = "full_name", nullable = false, length = 50)
  private String fullName;

  /**
   * Holds value of property start date.
   */
  @NotNull
  @Future
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  /**
   * Holds value of property end date.
   */
  @NotNull
  @Future
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  /**
   * Holds value of property active.
   */
  @Column(name = "active", nullable = false)
  private boolean active;

  /**
   * Checks if the booking is new.
   *
   * @return true if the booking is new, otherwise false
   */
  @JsonIgnore
  public boolean isNew() {
    return this.id == null;
  }

  /**
   * Returns a list of booking dates. The returned list starts from the booking start date
   * (inclusive) and goes to the booking end date (exclusive).
   *
   * @return a list of booking dates
   */
  @JsonIgnore
  public List<LocalDate> getBookingDates() {
    return this.startDate.datesUntil(this.endDate).collect(Collectors.toList());
  }

}
