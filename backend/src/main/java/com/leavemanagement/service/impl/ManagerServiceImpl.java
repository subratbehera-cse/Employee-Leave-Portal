package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.LeaveReviewRequest;
import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.entity.LeaveRequest;
import com.leavemanagement.entity.User;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.exception.BadRequestException;
import com.leavemanagement.exception.ForbiddenException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final UserRepository userRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserMapper userMapper;
    private final LeaveRequestMapper leaveRequestMapper;
    private final AuditLogService auditLogService;

    @Override
    public List<UserResponse> getTeamMembers(Long managerId) {
        return userRepository.findByManagerId(managerId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponse> getTeamLeaveRequests(Long managerId) {
        List<Long> teamIds = userRepository.findByManagerId(managerId).stream()
                .map(User::getId)
                .collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            return List.of();
        }

        return leaveRequestRepository.findByUserIdInOrderByAppliedAtDesc(teamIds).stream()
                .map(leaveRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveRequestResponse approveLeave(Long managerId, Long leaveId, LeaveReviewRequest request) {
        LeaveRequest leaveRequest = getTeamLeaveRequestOrThrow(managerId, leaveId);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be approved");
        }

        User employee = leaveRequest.getUser();
        int year = leaveRequest.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByUserIdAndLeaveTypeIdAndYear(employee.getId(), leaveRequest.getLeaveType().getId(), year)
                .orElseThrow(() -> new BadRequestException("No leave balance configured for this employee and leave type"));

        if (balance.getRemainingDays() < leaveRequest.getNumberOfDays()) {
            throw new BadRequestException("Employee no longer has sufficient leave balance for this request");
        }

        balance.setUsedDays(balance.getUsedDays() + leaveRequest.getNumberOfDays());
        balance.setRemainingDays(balance.getTotalDays() - balance.getUsedDays());
        leaveBalanceRepository.save(balance);

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setManagerComment(request != null ? request.getComment() : null);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setReviewedBy(userRepository.findById(managerId).orElse(null));
        leaveRequestRepository.save(leaveRequest);

        auditLogService.log(managerId, "APPROVE_LEAVE", "LeaveRequest", leaveId,
                "Approved leave request for " + employee.getFirstName() + " " + employee.getLastName());

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestResponse rejectLeave(Long managerId, Long leaveId, LeaveReviewRequest request) {
        LeaveRequest leaveRequest = getTeamLeaveRequestOrThrow(managerId, leaveId);

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new BadRequestException("Only pending leave requests can be rejected");
        }

        leaveRequest.setStatus(LeaveStatus.REJECTED);
        leaveRequest.setManagerComment(request != null ? request.getComment() : null);
        leaveRequest.setReviewedAt(LocalDateTime.now());
        leaveRequest.setReviewedBy(userRepository.findById(managerId).orElse(null));
        leaveRequestRepository.save(leaveRequest);

        auditLogService.log(managerId, "REJECT_LEAVE", "LeaveRequest", leaveId,
                "Rejected leave request for " + leaveRequest.getUser().getFirstName() + " " + leaveRequest.getUser().getLastName());

        return leaveRequestMapper.toResponse(leaveRequest);
    }

    private LeaveRequest getTeamLeaveRequestOrThrow(Long managerId, Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + leaveId));

        if (leaveRequest.getUser().getId().equals(managerId)) {
            throw new ForbiddenException("You cannot approve or reject your own leave request");
        }

        User manager = leaveRequest.getUser().getManager();
        if (manager == null || !manager.getId().equals(managerId)) {
            throw new ForbiddenException("You can only review leave requests from your own team");
        }

        return leaveRequest;
    }
}
