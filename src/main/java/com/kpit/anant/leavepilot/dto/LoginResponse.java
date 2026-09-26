package com.kpit.anant.leavepilot.dto;

public record LoginResponse(String token, Integer id, String fullName, String email, String role,
        String department, String status) {
}
