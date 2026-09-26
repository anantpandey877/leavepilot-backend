package com.kpit.anant.leavepilot.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LeaveRequestDto(
        Integer userId,
        @NotBlank String leaveType,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        String reason) {
}
