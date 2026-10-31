package com.example.ticketing.controller;

import com.example.ticketing.dto.ApiResponse;
import com.example.ticketing.dto.AuthRequest;
import com.example.ticketing.dto.AuthResponse;
import com.example.ticketing.dto.RegisterRequest;
import com.example.ticketing.entity.User;
import com.example.ticketing.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder().success(true).message("Registration successful").data(response).build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder().success(true).message("Login successful").data(response).build());
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> me(@AuthenticationPrincipal UserDetails principal) {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.<User>builder().success(true).message("Current user").data(user).build());
    }
}
