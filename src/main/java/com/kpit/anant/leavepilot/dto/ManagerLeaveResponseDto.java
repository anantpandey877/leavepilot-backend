package com.kpit.anant.leavepilot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kpit.anant.leavepilot.model.LeaveRequest;

public record ManagerLeaveResponseDto(Integer leaveId, Integer userId, String fullName, String email,
        String department, String role, String leaveType, LocalDate startDate, LocalDate endDate,
        String reason, String status, LocalDateTime appliedAt) {
    public static ManagerLeaveResponseDto from(LeaveRequest leave) {
        var user = leave.getUser();
        return new ManagerLeaveResponseDto(leave.getId(), user.getId(), user.getFullName(), user.getEmail(),
                user.getDepartment().name(), user.getRole().name(), leave.getLeaveType().name(), leave.getStartDate(),
                leave.getEndDate(), leave.getReason(), leave.getStatus().name(), leave.getAppliedAt());
    }
}
