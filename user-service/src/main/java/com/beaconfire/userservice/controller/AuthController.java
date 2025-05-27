package com.beaconfire.userservice.controller;


import com.beaconfire.userservice.dto.UserRegisterDTO;
import com.beaconfire.userservice.request.LoginRequest;
import com.beaconfire.userservice.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest loginRequest,
                                                    HttpServletResponse response){
        try{
            String token = userService.validateUserAndIssueToken(loginRequest);
            // Save token into httpOnly cookie.
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
//            cookie.setSecure(true);        // only for https
            cookie.setMaxAge(36000);    // 10 hours
            response.addCookie(cookie);
            return ResponseEntity.ok(Map.of("message", "Login Successfully"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(
            @Valid @RequestBody UserRegisterDTO user,
            @RequestHeader(value = "X-User-Role", required = false) String userRole){
        if(userRole!=null && userRole.equals("admin")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Admin cannot register new user"));
        }
        try{
            userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
