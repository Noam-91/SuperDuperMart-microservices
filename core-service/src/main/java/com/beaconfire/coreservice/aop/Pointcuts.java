package com.beaconfire.coreservice.aop;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
public class Pointcuts {
    @Pointcut("within(com.beaconfire.coreservice.dao..*)")
    public void allDaoMethods() {}
    @Pointcut("within(com.beaconfire.coreservice.controller..*)")
    public void allControllerMethods() {}

    @Pointcut("within(com.beaconfire.coreservice.service.AggregatorService)")
    public void AggregatorServiceMethods() {}
}
