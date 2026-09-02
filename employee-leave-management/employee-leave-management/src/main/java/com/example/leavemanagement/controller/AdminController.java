package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.DecisionRequest;
import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LeaveService leaveService;

    public AdminController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/leaves")
    public List<LeaveRequest> allLeaves() {
        return leaveService.getAll();
    }

    @PutMapping("/leaves/{leaveId}/approve")
    public LeaveRequest approve(
            @PathVariable Long leaveId,
            @RequestParam Long adminId,
            @Valid @RequestBody(required = false) DecisionRequest request) {
        String comment = request == null ? null : request.comment();
        return leaveService.approve(leaveId, adminId, comment);
    }

    @PutMapping("/leaves/{leaveId}/reject")
    public LeaveRequest reject(
            @PathVariable Long leaveId,
            @RequestParam Long adminId,
            @Valid @RequestBody(required = false) DecisionRequest request) {
        String comment = request == null ? null : request.comment();
        return leaveService.reject(leaveId, adminId, comment);
    }
}
