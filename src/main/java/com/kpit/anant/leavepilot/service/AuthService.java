package com.kpit.anant.leavepilot.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kpit.anant.leavepilot.dto.LoginRequest;
import com.kpit.anant.leavepilot.dto.RegisterRequest;
import com.kpit.anant.leavepilot.model.Department;
import com.kpit.anant.leavepilot.model.Role;
import com.kpit.anant.leavepilot.model.User;
import com.kpit.anant.leavepilot.model.UserStatus;
import com.kpit.anant.leavepilot.repository.UserRepository;
import com.kpit.anant.leavepilot.security.JwtService;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public boolean register(RegisterRequest request) {
        if (users.findByEmail(request.email()).isPresent()) {
            return false;
        }
        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        Role role = parseRole(request.role());
        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("Admin registration is not allowed.");
        }
        user.setRole(role);
        user.setDepartment(parseDepartment(request.department()));
        user.setStatus(UserStatus.PENDING);
        users.save(user);
        return true;
    }

    public Optional<User> authenticate(LoginRequest request) {
        return users.findByEmail(request.email())
                .filter(user -> user.getStatus() == UserStatus.APPROVED)
                .filter(user -> passwordEncoder.matches(request.password(), user.getPasswordHash()));
    }

    public String tokenFor(User user) {
        return jwtService.generateToken(user);
    }

    private Role parseRole(String value) {
        return Role.valueOf(value.toUpperCase());
    }

    private Department parseDepartment(String value) {
        return Department.valueOf(value.toUpperCase());
    }
}
