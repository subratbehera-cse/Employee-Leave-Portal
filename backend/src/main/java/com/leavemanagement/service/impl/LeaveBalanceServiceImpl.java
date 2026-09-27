package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.LeaveBalanceRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.entity.LeaveBalance;
import com.leavemanagement.entity.LeaveType;
import com.leavemanagement.entity.User;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.LeaveBalanceMapper;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.repository.LeaveTypeRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final AuditLogService auditLogService;

    @Override
    public List<LeaveBalanceResponse> getAll() {
        return leaveBalanceRepository.findAll().stream()
                .map(leaveBalanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveBalanceResponse create(LeaveBalanceRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getUserId()));
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + request.getLeaveTypeId()));

        leaveBalanceRepository.findByUserIdAndLeaveTypeIdAndYear(request.getUserId(), request.getLeaveTypeId(), request.getYear())
                .ifPresent(b -> {
                    throw new DuplicateResourceException("A leave balance already exists for this employee, leave type and year");
                });

        LeaveBalance balance = LeaveBalance.builder()
                .user(user)
                .leaveType(leaveType)
                .totalDays(request.getTotalDays())
                .usedDays(0.0)
                .remainingDays(request.getTotalDays())
                .year(request.getYear())
                .build();
        balance = leaveBalanceRepository.save(balance);

        auditLogService.log(null, "CREATE_LEAVE_BALANCE", "LeaveBalance", balance.getId(),
                "Leave balance created for " + user.getEmail() + " (" + leaveType.getName() + ", " + request.getYear() + ")");

        return leaveBalanceMapper.toResponse(balance);
    }

    @Override
    @Transactional
    public LeaveBalanceResponse update(Long id, LeaveBalanceRequest request) {
        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found with id: " + id));

        balance.setTotalDays(request.getTotalDays());
        balance.setRemainingDays(request.getTotalDays() - balance.getUsedDays());
        balance = leaveBalanceRepository.save(balance);

        auditLogService.log(null, "UPDATE_LEAVE_BALANCE", "LeaveBalance", id, "Leave balance updated");

        return leaveBalanceMapper.toResponse(balance);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LeaveBalance balance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found with id: " + id));
        leaveBalanceRepository.delete(balance);
        auditLogService.log(null, "DELETE_LEAVE_BALANCE", "LeaveBalance", id, "Leave balance deleted");
    }
}
