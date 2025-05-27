package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.dto.OrderDetailDto;
import com.beaconfire.coreservice.service.OrderService;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("{orderId}")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getOrder(@PathVariable Long orderId,
                               @RequestHeader("X-User-Id") Long userId,
                               @RequestHeader("X-User-Role") String userRole) {
        OrderDetailDto order = orderService.getOrderById(orderId, userId, userRole);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                         @RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader("X-User-Role") String userRole) {
        orderService.cancelOrder(orderId, userId, userRole);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{orderId}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable Long orderId,
                                         @RequestHeader("X-User-Id") Long userId,
                                         @RequestHeader("X-User-Role") String userRole) {
        orderService.completeOrder(orderId, userId, userRole);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getAllMyOrders(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }




}
