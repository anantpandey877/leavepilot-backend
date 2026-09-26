package com.kpit.anant.leavepilot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kpit.anant.leavepilot.model.Department;
import com.kpit.anant.leavepilot.model.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByUserId(Integer userId);

    List<LeaveRequest> findByUserDepartmentOrderByAppliedAtDesc(Department department);

    Optional<LeaveRequest> findByIdAndUserId(Integer id, Integer userId);
}
