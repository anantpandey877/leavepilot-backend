package com.kpit.anant.leavepilot.dto;

import java.time.LocalDateTime;

import com.kpit.anant.leavepilot.model.User;

public record UserResponse(Integer id, String fullName, String email, String role, String department,
        String status, LocalDateTime createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole().name(),
                user.getDepartment().name(), user.getStatus().name(), user.getCreatedAt());
    }
}
