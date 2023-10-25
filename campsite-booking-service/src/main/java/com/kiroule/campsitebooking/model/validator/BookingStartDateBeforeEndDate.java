package com.kiroule.campsitebooking.model.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Igor Baiborodine
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BookingStartDateBeforeEndDateValidator.class})
@Documented
public @interface BookingStartDateBeforeEndDate {

  String message() default "Booking start date must be before end date";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
