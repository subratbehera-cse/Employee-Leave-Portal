package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.LeaveRequestCreateRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.entity.LeaveRequest;
import com.leavemanagement.entity.LeaveType;
import com.leavemanagement.entity.User;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.*;
import com.leavemanagement.mapper.LeaveBalanceMapper;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.repository.LeaveTypeRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public LeaveRequestResponse applyLeave(Long userId, LeaveRequestCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + request.getLeaveTypeId()));

        if (!Boolean.TRUE.equals(leaveType.getIsActive())) {
            throw new InvalidLeaveRequestException("The selected leave type is not currently active");
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new InvalidLeaveRequestException("Start date cannot be after end date");
        }

        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlapping(
                userId, request.getStartDate(), request.getEndDate());
        if (!overlapping.isEmpty()) {
            throw new InvalidLeaveRequestException("You already have a pending or approved leave request that overlaps with these dates");
        }

        double numberOfDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        int year = request.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeIdAndYear(userId, leaveType.getId(), year)
                .orElseThrow(() -> new InsufficientLeaveBalanceException(
                        "No leave balance configured for " + leaveType.getName() + " in " + year));

        if (balance.getRemainingDays() < numberOfDays) {
            throw new InsufficientLeaveBalanceException(
                    "Insufficient leave balance. Remaining: " + balance.getRemainingDays() + ", requested: " + numberOfDays);
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .user(user)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays(numberOfDays)
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        leaveRequest = leaveRequestRepository.save(leaveRequest);

        auditLogService.log(userId, "APPLY_LEAVE", "LeaveRequest", leaveRequest.getId(),
                "Applied for " + leaveType.getName() + " from " + request.getStartDate() + " to " + request.getEndDate());

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponse> getMyLeaves(Long userId) {
        return leaveRequestRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
                .map(leaveRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponse getLeaveById(Long userId, String role, Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        boolean isOwner = leaveRequest.getUser().getId().equals(userId);
        boolean isPrivileged = "ADMIN".equals(role) || "MANAGER".equals(role);

        if (!isOwner && !isPrivileged) {
            throw new ForbiddenException("You are not allowed to view this leave request");
        }

        // Managers may only view requests belonging to their own team (or themselves); admins see all.
        if (!isOwner && "MANAGER".equals(role)) {
            User manager = leaveRequest.getUser().getManager();
            if (manager == null || !manager.getId().equals(userId)) {
                throw new ForbiddenException("You are not allowed to view this leave request");
            }
        }

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestResponse cancelLeave(Long userId, Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        if (!leaveRequest.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own leave requests");
        }

        if (leaveRequest.getStatus() == LeaveStatus.CANCELLED) {
            throw new BadRequestException("This leave request has already been cancelled");
        }

        if (leaveRequest.getStatus() == LeaveStatus.REJECTED) {
            throw new BadRequestException("A rejected leave request cannot be cancelled");
        }

        boolean wasApproved = leaveRequest.getStatus() == LeaveStatus.APPROVED;

        if (wasApproved) {
            // Restore the balance that was consumed on approval.
            int year = leaveRequest.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository
                    .findByUserIdAndLeaveTypeIdAndYear(userId, leaveRequest.getLeaveType().getId(), year)
                    .orElse(null);
            if (balance != null) {
                balance.setUsedDays(Math.max(0, balance.getUsedDays() - leaveRequest.getNumberOfDays()));
                balance.setRemainingDays(balance.getTotalDays() - balance.getUsedDays());
                leaveBalanceRepository.save(balance);
            }
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRequest.setReviewedAt(java.time.LocalDateTime.now());
        leaveRequestRepository.save(leaveRequest);

        auditLogService.log(userId, "CANCEL_LEAVE", "LeaveRequest", leaveId,
                "Cancelled leave request" + (wasApproved ? " and restored leave balance" : ""));

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Override
    public List<LeaveBalanceResponse> getMyBalances(Long userId) {
        int year = Year.now().getValue();
        return leaveBalanceRepository.findByUserIdAndYear(userId, year).stream()
                .map(leaveBalanceMapper::toResponse)
                .collect(Collectors.toList());
    }
}
