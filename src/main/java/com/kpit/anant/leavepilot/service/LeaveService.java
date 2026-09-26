package com.kpit.anant.leavepilot.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kpit.anant.leavepilot.dto.LeaveRequestDto;
import com.kpit.anant.leavepilot.model.Department;
import com.kpit.anant.leavepilot.model.LeaveBalance;
import com.kpit.anant.leavepilot.model.LeaveRequest;
import com.kpit.anant.leavepilot.model.LeaveStatus;
import com.kpit.anant.leavepilot.model.LeaveType;
import com.kpit.anant.leavepilot.model.User;
import com.kpit.anant.leavepilot.repository.LeaveBalanceRepository;
import com.kpit.anant.leavepilot.repository.LeaveRequestRepository;
import com.kpit.anant.leavepilot.repository.UserRepository;

@Service
public class LeaveService {
    private final LeaveRequestRepository leaves;
    private final LeaveBalanceRepository balances;
    private final UserRepository users;

    public LeaveService(LeaveRequestRepository leaves, LeaveBalanceRepository balances, UserRepository users) {
        this.leaves = leaves;
        this.balances = balances;
        this.users = users;
    }

    public LeaveBalance balance(Integer userId) {
        return balances.findByUserId(userId).orElse(null);
    }

    public List<LeaveRequest> byUser(Integer userId) {
        return leaves.findByUserId(userId);
    }

    @Transactional
    public boolean apply(LeaveRequestDto request) {
        if (request.userId() == null || request.endDate().isBefore(request.startDate())) {
            return false;
        }
        User user = users.findById(request.userId()).orElse(null);
        LeaveBalance balance = balance(request.userId());
        if (user == null || balance == null) {
            return false;
        }
        LeaveType type;
        try {
            type = LeaveType.valueOf(request.leaveType().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return false;
        }
        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        if (available(balance, type) < days) {
            return false;
        }
        LeaveRequest leave = new LeaveRequest();
        leave.setUser(user);
        leave.setLeaveType(type);
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setReason(request.reason());
        leave.setStatus(LeaveStatus.PENDING);
        leaves.save(leave);
        return true;
    }

    public List<LeaveRequest> byDepartment(String department) {
        return leaves.findByUserDepartmentOrderByAppliedAtDesc(Department.valueOf(department.toUpperCase()));
    }

    @Transactional
    public boolean review(Integer leaveId, Integer managerId, boolean approve) {
        LeaveRequest leave = leaves.findById(leaveId).orElse(null);
        User manager = users.findById(managerId).orElse(null);
        if (leave == null || manager == null || leave.getStatus() != LeaveStatus.PENDING) {
            return false;
        }
        if (manager.getRole() != com.kpit.anant.leavepilot.model.Role.MANAGER
                || manager.getDepartment() != leave.getUser().getDepartment()) {
            return false;
        }
        leave.setReviewer(manager);
        leave.setReviewedAt(LocalDateTime.now());
        if (!approve) {
            leave.setStatus(LeaveStatus.REJECTED);
            return true;
        }
        int days = (int) (ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1);
        LeaveBalance balance = balance(leave.getUser().getId());
        if (balance == null || available(balance, leave.getLeaveType()) < days) {
            return false;
        }
        deduct(balance, leave.getLeaveType(), days);
        leave.setStatus(LeaveStatus.APPROVED);
        return true;
    }

    @Transactional
    public boolean cancel(Integer leaveId) {
        return leaves.findById(leaveId).map(leave -> {
            if (leave.getStatus() != LeaveStatus.PENDING) {
                return false;
            }
            leave.setStatus(LeaveStatus.CANCELLED);
            return true;
        }).orElse(false);
    }

    private int available(LeaveBalance balance, LeaveType type) {
        return switch (type) {
            case CASUAL -> balance.getCasualLeave();
            case SICK -> balance.getSickLeave();
            case EARNED -> balance.getEarnedLeave();
        };
    }

    private void deduct(LeaveBalance balance, LeaveType type, int days) {
        switch (type) {
            case CASUAL -> balance.setCasualLeave(balance.getCasualLeave() - days);
            case SICK -> balance.setSickLeave(balance.getSickLeave() - days);
            case EARNED -> balance.setEarnedLeave(balance.getEarnedLeave() - days);
        }
    }
}
