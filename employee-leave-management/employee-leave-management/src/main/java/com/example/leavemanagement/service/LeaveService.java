package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.CreateLeaveRequest;
import com.example.leavemanagement.entity.LeaveRequest;
import com.example.leavemanagement.entity.LeaveStatus;
import com.example.leavemanagement.entity.Role;
import com.example.leavemanagement.entity.User;
import com.example.leavemanagement.exception.BadRequestException;
import com.example.leavemanagement.exception.ResourceNotFoundException;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveService {

    private final LeaveRequestRepository leaveRepository;
    private final UserRepository userRepository;

    public LeaveService(LeaveRequestRepository leaveRepository, UserRepository userRepository) {
        this.leaveRepository = leaveRepository;
        this.userRepository = userRepository;
    }

    public LeaveRequest create(Long employeeId, CreateLeaveRequest request) {
        User employee = getEmployee(employeeId);

        if (request.endDate().isBefore(request.startDate())) {
            throw new BadRequestException("End date must be on or after start date");
        }

        if (request.startDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Leave cannot start in the past");
        }

        boolean overlapping = leaveRepository
                .existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        employeeId,
                        List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                        request.endDate(),
                        request.startDate()
                );

        if (overlapping) {
            throw new BadRequestException("Leave dates overlap an existing pending/approved request");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setStartDate(request.startDate());
        leave.setEndDate(request.endDate());
        leave.setReason(request.reason().trim());
        leave.setStatus(LeaveStatus.PENDING);

        return leaveRepository.save(leave);
    }

    public List<LeaveRequest> history(Long employeeId) {
        getEmployee(employeeId);
        return leaveRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    public LeaveRequest getById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + id));
    }

    public List<LeaveRequest> getAll() {
        return leaveRepository.findAllByOrderByStartDateDesc();
    }

    public LeaveRequest approve(Long leaveId, Long adminId, String comment) {
        verifyAdmin(adminId);
        LeaveRequest leave = getById(leaveId);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setAdminComment(comment);
        return leaveRepository.save(leave);
    }

    public LeaveRequest reject(Long leaveId, Long adminId, String comment) {
        verifyAdmin(adminId);
        LeaveRequest leave = getById(leaveId);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setAdminComment(comment);
        return leaveRepository.save(leave);
    }

    public LeaveRequest cancel(Long leaveId, Long employeeId) {
        LeaveRequest leave = getById(leaveId);

        if (!leave.getEmployee().getId().equals(employeeId)) {
            throw new BadRequestException("You can only cancel your own leave request");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        return leaveRepository.save(leave);
    }

    private User getEmployee(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (user.getRole() != Role.EMPLOYEE) {
            throw new BadRequestException("User is not an employee");
        }
        return user;
    }

    private void verifyAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + adminId));

        if (admin.getRole() != Role.ADMIN) {
            throw new BadRequestException("Only admins can perform this action");
        }
    }
}
