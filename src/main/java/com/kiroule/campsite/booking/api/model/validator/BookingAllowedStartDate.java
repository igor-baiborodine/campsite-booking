package com.kiroule.campsite.booking.api.model.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * @author Igor Baiborodine
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BookingAllowedStartDateValidator.class})
@Documented
public @interface BookingAllowedStartDate {

  String message() default "Booking start date must be from 1 day to up to 1 month ahead";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
