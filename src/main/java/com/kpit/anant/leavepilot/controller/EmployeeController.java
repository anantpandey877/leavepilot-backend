package com.kpit.anant.leavepilot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kpit.anant.leavepilot.dto.ApiResponse;
import com.kpit.anant.leavepilot.dto.LeaveBalanceResponse;
import com.kpit.anant.leavepilot.dto.LeaveRequestDto;
import com.kpit.anant.leavepilot.dto.LeaveResponse;
import com.kpit.anant.leavepilot.model.LeaveBalance;
import com.kpit.anant.leavepilot.service.LeaveService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("!isAuthenticated() || hasRole('EMPLOYEE')")
public class EmployeeController {
    private final LeaveService leaveService;

    public EmployeeController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> balance(@RequestParam Integer userId) {
        LeaveBalance balance = leaveService.balance(userId);
        return balance == null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(LeaveBalanceResponse.from(balance));
    }

    @GetMapping("/leaves")
    public List<LeaveResponse> leaves(@RequestParam Integer userId) {
        return leaveService.byUser(userId).stream().map(LeaveResponse::from).toList();
    }

    @PostMapping("/leaves")
    public ResponseEntity<ApiResponse> apply(@Valid @RequestBody LeaveRequestDto request) {
        if (!leaveService.apply(request)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Insufficient leave balance"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Leave applied successfully"));
    }

    @PutMapping("/leaves/{id}/cancel")
    public ResponseEntity<ApiResponse> cancel(@PathVariable Integer id) {
        boolean success = leaveService.cancel(id);
        return ResponseEntity
                .ok(new ApiResponse(success, success ? "Leave cancelled successfully" : "Failed to cancel leave"));
    }
}
