package com.beaconfire.coreservice.controller;

import com.beaconfire.coreservice.service.WatchlistService;
import com.beaconfire.coreservice.view.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/watchlist")
public class WatchlistController {
    private final WatchlistService watchlistService;
    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping("/all")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getAllMyWatchlists(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(watchlistService.getWatchlistsByUserId(userId));
    }

    @GetMapping("{watchlistId}")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> getWatchlistById(@PathVariable("watchlistId") Long watchlistId,
                                                  @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(watchlistService.getWatchlistById(watchlistId, userId));
    }

    @PostMapping("{watchlistId}/add/{productId}")
    public ResponseEntity<?> addProductToWatchlist(@PathVariable("watchlistId") Long watchlistId,
                                                   @PathVariable("productId") Long productId,
                                                   @RequestHeader("X-User-Id") Long userId) {
        watchlistService.addProductToWatchlist(watchlistId, productId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{watchlistId}/remove/{productId}")
    public ResponseEntity<?> removeProductFromWatchlist(@PathVariable("watchlistId") Long watchlistId,
                                                        @PathVariable("productId") Long productId,
                                                        @RequestHeader("X-User-Id") Long userId) {
        watchlistService.removeProductFromWatchlist(watchlistId, productId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    @JsonView(Views.Public.class)
    public ResponseEntity<?> createWatchlist(@RequestHeader("X-User-Id") Long userId,
                                             @RequestParam(name = "name") String name){
        return ResponseEntity.ok(watchlistService.createWatchlist(userId, name));
    }

    @DeleteMapping("/delete/{watchlistId}")
    public ResponseEntity<?> deleteWatchlist(@PathVariable(name = "watchlistId") Long watchlistId,
                                             @RequestHeader("X-User-Id") Long userId){
        watchlistService.deleteWatchlist(watchlistId, userId);
        return ResponseEntity.ok().build();
    }
}
