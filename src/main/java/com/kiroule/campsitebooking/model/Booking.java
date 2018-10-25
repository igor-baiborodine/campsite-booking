package com.kiroule.campsitebooking.model;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Simple JavaBean domain object representing a booking.
 *
 * @author Igor Baiborodine
 */
@Data
@Entity
@Table(name = "bookings")
public class Booking {

  /** Holds value of property id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** Holds value of property email. */
  @NotEmpty
  @Column(name = "email", nullable = false)
  private String email;

  /** Holds value of property full name. */
  @NotEmpty
  @Column(name = "full_name", nullable = false)
  private String fullName;

  /** Holds value of property start date. */
  @NotNull
  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = "yyyy-MM-dd") // ISO 8601
  private Date startDate;

  /** Holds value of property end date. */
  @NotNull
  @Column(name = "end_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @DateTimeFormat(pattern = "yyyy-MM-dd") // ISO 8601
  private Date endDate;

  /** Holds value of property active */
  @NotNull
  @Column(name = "active", nullable = false)
  @Type(type = "yes_no")
  private Boolean active = Boolean.TRUE;
}
