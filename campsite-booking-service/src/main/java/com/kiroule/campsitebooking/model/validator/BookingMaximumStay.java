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
@Constraint(validatedBy = {BookingMaximumStayValidator.class})
@Documented
public @interface BookingMaximumStay {

  String message() default "Booking stay length must be less or equal to three days";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
