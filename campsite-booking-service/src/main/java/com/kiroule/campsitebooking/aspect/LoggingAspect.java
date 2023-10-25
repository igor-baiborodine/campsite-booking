package com.kiroule.campsitebooking.aspect;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Igor Baiborodine
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

  @Pointcut("execution(public * com.kiroule.campsite.booking.api.service..*(..))")
  private void serviceLoggingTargets() {
    // marker method, should be empty
  }

  @Before("serviceLoggingTargets()")
  public void logEnterMethod(JoinPoint jp) {
    var args = Stream.of(jp.getArgs()).map(Object::toString).collect(Collectors.joining(", "));
    log.info("{}: args[{}]", jp.getSignature().toShortString(), args);
  }

  @AfterReturning(value = "serviceLoggingTargets()", returning = "result")
  public void logExitMethod(JoinPoint jp, Object result) {
    log.info("{}: result[{}]", jp.getSignature().toShortString(), result);
  }

  @AfterThrowing(value = "execution(public * com.kiroule.campsite.booking..*(..))", throwing = "ex")
  public void logThrowException(JoinPoint jp, Exception ex) {
    log.error("{}: error[{}]", jp.getSignature().toShortString(), ex.getMessage(), ex);
  }
}
