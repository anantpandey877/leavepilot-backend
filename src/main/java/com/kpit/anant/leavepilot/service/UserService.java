package com.kpit.anant.leavepilot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kpit.anant.leavepilot.model.LeaveBalance;
import com.kpit.anant.leavepilot.model.Role;
import com.kpit.anant.leavepilot.model.User;
import com.kpit.anant.leavepilot.model.UserStatus;
import com.kpit.anant.leavepilot.repository.LeaveBalanceRepository;
import com.kpit.anant.leavepilot.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository users;
    private final LeaveBalanceRepository balances;

    public UserService(UserRepository users, LeaveBalanceRepository balances) {
        this.users = users;
        this.balances = balances;
    }

    public List<User> pendingUsers() {
        return users.findByStatus(UserStatus.PENDING);
    }

    public List<User> allUsers() {
        return users.findAll();
    }

    @Transactional
    public boolean updateStatus(Integer id, UserStatus status) {
        return users.findById(id).map(user -> {
            user.setStatus(status);
            if (status == UserStatus.APPROVED && user.getRole() == Role.EMPLOYEE
                    && balances.findByUserId(id).isEmpty()) {
                LeaveBalance balance = new LeaveBalance();
                balance.setUser(user);
                balances.save(balance);
            }
            return true;
        }).orElse(false);
    }
}
