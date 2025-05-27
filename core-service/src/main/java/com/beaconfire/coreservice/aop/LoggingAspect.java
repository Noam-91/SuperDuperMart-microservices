package com.beaconfire.coreservice.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("com.beaconfire.coreservice.aop.Pointcuts.allControllerMethods()")
    public void logControllerMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        LOGGER.info("Controller method called: {}.{}", className, methodName);
    }

    @After("com.beaconfire.coreservice.aop.Pointcuts.AggregatorServiceMethods()")
    public void logServiceMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        LOGGER.info("Aggregator Service method called: {}.{}", className, methodName);
    }
}
