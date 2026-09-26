package com.kpit.anant.leavepilot.dto;

import com.kpit.anant.leavepilot.model.LeaveBalance;

public record LeaveBalanceResponse(Integer id, Integer userId, int casualLeave, int sickLeave, int earnedLeave) {
    public static LeaveBalanceResponse from(LeaveBalance balance) {
        return new LeaveBalanceResponse(balance.getId(), balance.getUser().getId(), balance.getCasualLeave(),
                balance.getSickLeave(), balance.getEarnedLeave());
    }
}
