package com.beaconfire.catalogservice.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LoggingAspect.class);


    @Before("execution(* com.beaconfire.catalogservice.controller.ProductInternalController.getProductsByIds)")
    public void beforeGetProductsByIds() {
        LOGGER.info("Before Webclient Request");
    }

    @Before("bean(ProductCacheService)")
    public void beforeProductCacheService(JoinPoint joinPoint) {
        LOGGER.info("Access cache: {}", joinPoint.getSignature().getName());
    }
}
