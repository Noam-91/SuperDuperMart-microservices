package com.beaconfire.apigateway.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class AuthStatusController {
    @GetMapping("/auth-status")
    public ResponseEntity<Map<String, String>> getAuthStatus(@RequestHeader("X-User-Id") String userId,
                                             @RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(Map.of("userId", userId, "userRole", userRole));
    }

    @PostMapping("/logout")
    public Mono<Void> logout(ServerHttpResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("token","")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        response.addCookie(deleteCookie);
        return response.setComplete();
    }
}
