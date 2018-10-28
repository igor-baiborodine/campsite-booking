package com.kiroule.campsitebooking.model;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class Booking {

  /**
   * Holds value of property id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Holds value of property email.
   */
  @NotEmpty
  @Column(name = "email", nullable = false, length = 50)
  private String email;

  /**
   * Holds value of property full name.
   */
  @NotEmpty
  @Column(name = "full_name", nullable = false, length = 50)
  private String fullName;

  /**
   * Holds value of property start date.
   */
  @NotNull
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  /**
   * Holds value of property end date.
   */
  @NotNull
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  /**
   * Holds value of property active
   */
  @NotNull
  @Column(name = "active", nullable = false)
  private boolean active;
}
