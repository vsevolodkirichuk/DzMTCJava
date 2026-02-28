package com.example.todolist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP aspect that intercepts all methods in the {@code service} package
 * and logs their invocation details and results using {@code @Around} advice.
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  /**
   * Intercepts all methods in the service package, logs start/end and the returned value.
   *
   * @param joinPoint the intercepted method context
   * @return the result of the intercepted method
   * @throws Throwable if the intercepted method throws
   */
  @Around("execution(* com.example.todolist.service.*.*(..))")
  public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    log.info("[AOP] START {}.{}() | args: {}", className, methodName, Arrays.toString(args));

    Object result;
    try {
      result = joinPoint.proceed();
    } catch (Throwable ex) {
      log.error("[AOP] EXCEPTION in {}.{}() | message: {}", className, methodName, ex.getMessage());
      throw ex;
    }

    if (result == null) {
      log.info("[AOP] END {}.{}() | result: void", className, methodName);
    } else {
      log.info("[AOP] END {}.{}() | result: {}", className, methodName, result);
    }

    return result;
  }
}
