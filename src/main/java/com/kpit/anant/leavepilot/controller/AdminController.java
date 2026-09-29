package com.kpit.anant.leavepilot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kpit.anant.leavepilot.dto.ApiResponse;
import com.kpit.anant.leavepilot.dto.UserResponse;
import com.kpit.anant.leavepilot.model.UserStatus;
import com.kpit.anant.leavepilot.service.UserService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/pending-users")
    public List<UserResponse> pendingUsers() {
        return userService.pendingUsers().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.allUsers().stream().map(UserResponse::from).toList();
        //rest
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Integer id) {
        return update(id, UserStatus.APPROVED, "User approved successfully", "User approval failed");
    }

    @PutMapping("/users/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Integer id) {
        return update(id, UserStatus.REJECTED, "User rejected successfully", "User rejection failed");
    }

    private ResponseEntity<ApiResponse> update(Integer id, UserStatus status, String successMessage,
            String failureMessage) {
        boolean success = userService.updateStatus(id, status);
        return ResponseEntity.status(success ? 200 : 400)
                .body(new ApiResponse(success, success ? successMessage : failureMessage));
    }
}
