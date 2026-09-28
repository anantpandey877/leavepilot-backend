package com.kpit.anant.leavepilot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kpit.anant.leavepilot.dto.ApiResponse;
import com.kpit.anant.leavepilot.dto.ManagerLeaveResponseDto;
import com.kpit.anant.leavepilot.service.LeaveService;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {
    private final LeaveService leaveService;

    public ManagerController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/leaves")
    public List<ManagerLeaveResponseDto> leaves(@RequestParam String department) {
        return leaveService.byDepartment(department).stream().map(ManagerLeaveResponseDto::from).toList();
    }

    @PutMapping("/leaves/{id}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Integer id, @RequestParam Integer managerId) {
        return review(id, managerId, true);
    }

    @PutMapping("/leaves/{id}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Integer id, @RequestParam Integer managerId) {
        return review(id, managerId, false);
    }

    private ResponseEntity<ApiResponse> review(Integer id, Integer managerId, boolean approve) {
        boolean success = leaveService.review(id, managerId, approve);
        String action = approve ? "approval" : "rejection";
        String successMessage = approve ? "Leave approved successfully" : "Leave rejected successfully";
        return ResponseEntity.status(success ? 200 : 400)
                .body(new ApiResponse(success, success ? successMessage : "Leave " + action + " failed"));
    }
}
