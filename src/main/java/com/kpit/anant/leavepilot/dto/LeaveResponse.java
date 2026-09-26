package com.kpit.anant.leavepilot.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kpit.anant.leavepilot.model.LeaveRequest;

public record LeaveResponse(Integer id, Integer userId, String leaveType, LocalDate startDate, LocalDate endDate,
        String reason, String status, Integer reviewedBy, LocalDateTime appliedAt, LocalDateTime reviewedAt) {
    public static LeaveResponse from(LeaveRequest leave) {
        return new LeaveResponse(leave.getId(), leave.getUser().getId(), leave.getLeaveType().name(),
                leave.getStartDate(),
                leave.getEndDate(), leave.getReason(), leave.getStatus().name(),
                leave.getReviewer() == null ? null : leave.getReviewer().getId(), leave.getAppliedAt(),
                leave.getReviewedAt());
    }
}
