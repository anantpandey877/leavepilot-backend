package com.kpit.anant.leavepilot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kpit.anant.leavepilot.model.Department;
import com.kpit.anant.leavepilot.model.User;
import com.kpit.anant.leavepilot.model.UserStatus;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> findByStatus(UserStatus status);

    List<User> findByDepartment(Department department);
}
