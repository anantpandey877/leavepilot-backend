package com.kpit.anant.leavepilot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kpit.anant.leavepilot.dto.ApiResponse;
import com.kpit.anant.leavepilot.dto.LoginRequest;
import com.kpit.anant.leavepilot.dto.LoginResponse;
import com.kpit.anant.leavepilot.dto.RegisterRequest;
import com.kpit.anant.leavepilot.model.User;
import com.kpit.anant.leavepilot.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            if (!authService.register(request)) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "Email already exists."));
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "Registration submitted successfully. Awaiting admin approval."));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Invalid role or department."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var user = authService.authenticate(request);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid credentials or account not approved."));
        }
        return ResponseEntity.ok(toResponse(user.get()));
    }

    private LoginResponse toResponse(User user) {
        return new LoginResponse(authService.tokenFor(user), user.getId(), user.getFullName(), user.getEmail(),
                user.getRole().name(), user.getDepartment().name(), user.getStatus().name());
    }
}
