package com.kpit.anant.leavepilot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kpit.anant.leavepilot.model.LeaveBalance;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Integer> {
    Optional<LeaveBalance> findByUserId(Integer userId);
}
