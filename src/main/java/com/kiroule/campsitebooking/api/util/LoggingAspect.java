package com.kiroule.campsitebooking.api.util;

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

  @Pointcut("execution(public * com.kiroule.campsitebooking.service..*(..))")
  private void serviceLoggingTargets() {}

  @Before("serviceLoggingTargets()")
  public void logEnterMethod(JoinPoint jp) {
    String args = Stream.of(jp.getArgs())
        .map(Object::toString)
        .collect(Collectors.joining(", "));
    log.info("{}: args[{}]", jp.getSignature().toShortString(), args);
  }

  @AfterReturning(value = "serviceLoggingTargets()", returning = "result")
  public void logExitMethod(JoinPoint jp, Object result) {
    log.info("{}: result[{}]", jp.getSignature().toShortString(), result);
  }

  @AfterThrowing(
      value = "execution(public * com.kiroule.campsitebooking..*(..))", throwing = "ex")
  public void logThrowException(JoinPoint jp, Exception ex) {
    log.error("{}: error[{}]", jp.getSignature().toShortString(), ex.getMessage(), ex);
  }

}
