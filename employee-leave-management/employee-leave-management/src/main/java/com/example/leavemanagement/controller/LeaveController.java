package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.CreateLeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping("/employee/{employeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest create(
            @PathVariable Long employeeId,
            @Valid @RequestBody CreateLeaveRequest request) {
        return leaveService.create(employeeId, request);
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> history(@PathVariable Long employeeId) {
        return leaveService.history(employeeId);
    }

    @GetMapping("/{id}")
    public LeaveRequest get(@PathVariable Long id) {
        return leaveService.getById(id);
    }

    @PutMapping("/{id}/cancel")
    public LeaveRequest cancel(
            @PathVariable Long id,
            @RequestParam Long employeeId) {
        return leaveService.cancel(id, employeeId);
    }
}
