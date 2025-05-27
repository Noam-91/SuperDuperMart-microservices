package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.domain.complementary.Product;
import com.beaconfire.coreservice.service.OrderService;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/stats")
public class StatsController {
    private final OrderService orderService;
    public StatsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/frequent/{num}")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getMostFrequentPurchasedProducts(@PathVariable Integer num,
                                                              @RequestHeader("X-User-Id") Long viewerId) {
        List<Product> products = orderService.getMostFrequentPurchasedProducts(num, viewerId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/recent/{num}")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getMostRecentPurchasedProducts(@PathVariable Integer num,
                                                            @RequestHeader("X-User-Id") Long viewerId) {
        List<Product> products = orderService.getMostRecentPurchasedProducts(num, viewerId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/profit/{num}")
    @JsonView(Views.Internal.class)
    public ResponseEntity<?> getMostProfitableProducts(@PathVariable Integer num,
                                                       @RequestHeader("X-User-Role") String userRole) {
        List<Product> products = orderService.getMostProfitableProducts(num, userRole);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/popular/{num}")
    @JsonView(Views.Internal.class)
    public ResponseEntity<?> getMostPopularProducts(@PathVariable Integer num,
                                                    @RequestHeader("X-User-Role") String userRole) {
        List<Product> products = orderService.getMostPopularProducts(num, userRole);
        return ResponseEntity.ok(products);
    }
}
