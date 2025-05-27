package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.dto.OrderDetailDto;
import com.beaconfire.coreservice.service.OrderService;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/orders")
public class OrderInternalController {
    private final OrderService orderService;
    public OrderInternalController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("{orderId}")
    @JsonView(Views.Internal.class)
    public ResponseEntity<?> getOrderInternal(@PathVariable Long orderId,
                                              @RequestHeader("X-User-Id") Long userId,
                                              @RequestHeader("X-User-Role") String userRole) {
        OrderDetailDto order = orderService.getOrderById(orderId, userId, userRole);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/all")
    @JsonView(Views.Internal.class)
    public ResponseEntity<?> getAllOrdersInternal(@RequestHeader("X-User-Id") Long userId,
                                                  @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(orderService.getAllOrders(userId, userRole));
    }
}
