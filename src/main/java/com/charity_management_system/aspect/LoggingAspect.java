package com.charity_management_system.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut that matches all methods in the service layer.
     */
    @Pointcut("execution(* com.charity_management_system.service.*.*(..))")
    public void serviceLayerExecution() {}

    /**
     * Logs method entry and its arguments before execution.
     *
     * @param joinPoint The join point providing access to the method signature and arguments.
     */
    @Before("serviceLayerExecution()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    /**
     * Logs method exit and return value after execution.
     *
     * @param joinPoint The join point providing access to the method signature.
     * @param result    The return value of the method.
     */
    @AfterReturning(pointcut = "serviceLayerExecution()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Exiting method: {} with return value: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs any exception thrown by a method in the service layer.
     *
     * @param joinPoint The join point providing access to the method signature.
     * @param error     The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "serviceLayerExecution()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in method: {} with message: {}", joinPoint.getSignature().toShortString(), error.getMessage());
    }

    /**
     * Logs the execution time of a method.
     *
     * @param joinPoint The join point providing access to the method signature.
     * @return The result of the method execution.
     * @throws Throwable Any exceptions thrown by the method.
     */
    @Around("serviceLayerExecution()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), elapsedTime);
        return result;
    }
}
