package com.motive.numberverification.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging method entries, exits, and execution times.
 */
@Aspect
@Component
public class LoggingAspect {

    /**
     * Log method entry, exit, and execution time for all methods in the API, service, and integration layers.
     */
    @Around("execution(* com.motive.numberverification.api..*(..)) || " +
            "execution(* com.motive.numberverification.service..*(..)) || " +
            "execution(* com.motive.numberverification.integration..*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        
        // Get logger for the target class
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        
        // Build method signature string
        String methodName = signature.getMethod().getName();
        String className = signature.getDeclaringType().getSimpleName();
        String methodSignature = className + "." + methodName + "()";
        
        // Log method entry
        logger.debug("Entering: {}", methodSignature);
        
        // Create stopwatch for timing
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            // Execute the method
            Object result = joinPoint.proceed();
            
            // Log method exit and execution time
            stopWatch.stop();
            logger.debug("Exiting: {} (execution time: {} ms)", methodSignature, stopWatch.getTotalTimeMillis());
            
            return result;
        } catch (Exception e) {
            // Log method exception and execution time
            stopWatch.stop();
            logger.error("Exception in: {} (execution time: {} ms): {}", 
                    methodSignature, stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }
}
