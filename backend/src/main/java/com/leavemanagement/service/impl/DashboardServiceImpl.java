package com.leavemanagement.service.impl;

import com.leavemanagement.dto.response.*;
import com.leavemanagement.entity.LeaveRequest;
import com.leavemanagement.entity.User;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.mapper.LeaveBalanceMapper;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.repository.*;
import com.leavemanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestMapper leaveRequestMapper;
    private final LeaveBalanceMapper leaveBalanceMapper;

    @Override
    public EmployeeDashboardResponse getEmployeeDashboard(Long userId) {
        int year = Year.now().getValue();

        List<LeaveBalanceResponse> balances = leaveBalanceRepository.findByUserIdAndYear(userId, year).stream()
                .map(leaveBalanceMapper::toResponse)
                .collect(Collectors.toList());

        long pending = leaveRequestRepository.countByUserIdAndStatus(userId, LeaveStatus.PENDING);
        long approved = leaveRequestRepository.countByUserIdAndStatus(userId, LeaveStatus.APPROVED);
        long rejected = leaveRequestRepository.countByUserIdAndStatus(userId, LeaveStatus.REJECTED);

        List<LeaveRequest> upcoming = leaveRequestRepository.findUpcomingApproved(userId);
        LeaveRequestResponse upcomingLeave = upcoming.isEmpty() ? null : leaveRequestMapper.toResponse(upcoming.get(0));

        List<LeaveRequestResponse> recent = leaveRequestRepository.findByUserIdOrderByAppliedAtDesc(userId).stream()
                .limit(5)
                .map(leaveRequestMapper::toResponse)
                .collect(Collectors.toList());

        return EmployeeDashboardResponse.builder()
                .leaveBalances(balances)
                .pendingRequests(pending)
                .approvedRequests(approved)
                .rejectedRequests(rejected)
                .upcomingLeave(upcomingLeave)
                .recentRequests(recent)
                .build();
    }

    @Override
    public ManagerDashboardResponse getManagerDashboard(Long managerId) {
        List<Long> teamIds = userRepository.findByManagerId(managerId).stream()
                .map(User::getId)
                .collect(Collectors.toList());

        long teamSize = teamIds.size();
        long pending = teamIds.isEmpty() ? 0 : leaveRequestRepository.countByUserIdInAndStatus(teamIds, LeaveStatus.PENDING);
        long approved = teamIds.isEmpty() ? 0 : leaveRequestRepository.countByUserIdInAndStatus(teamIds, LeaveStatus.APPROVED);
        long rejected = teamIds.isEmpty() ? 0 : leaveRequestRepository.countByUserIdInAndStatus(teamIds, LeaveStatus.REJECTED);

        List<LeaveRequestResponse> teamRequests = teamIds.isEmpty() ? List.of() :
                leaveRequestRepository.findByUserIdInOrderByAppliedAtDesc(teamIds).stream()
                        .limit(10)
                        .map(leaveRequestMapper::toResponse)
                        .collect(Collectors.toList());

        return ManagerDashboardResponse.builder()
                .teamSize(teamSize)
                .pendingApprovals(pending)
                .approvedRequests(approved)
                .rejectedRequests(rejected)
                .teamLeaveRequests(teamRequests)
                .build();
    }

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        long totalEmployees = userRepository.count();
        long totalDepartments = departmentRepository.count();
        long pending = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
        long approved = leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);
        long rejected = leaveRequestRepository.countByStatus(LeaveStatus.REJECTED);

        List<LeaveRequest> all = leaveRequestRepository.findAllByOrderByAppliedAtDesc();

        Map<String, Long> distribution = new HashMap<>();
        for (LeaveRequest lr : all) {
            distribution.merge(lr.getLeaveType().getName(), 1L, Long::sum);
        }

        List<LeaveRequestResponse> recent = all.stream()
                .limit(10)
                .map(leaveRequestMapper::toResponse)
                .collect(Collectors.toList());

        return AdminDashboardResponse.builder()
                .totalEmployees(totalEmployees)
                .totalDepartments(totalDepartments)
                .pendingRequests(pending)
                .approvedRequests(approved)
                .rejectedRequests(rejected)
                .leaveTypeDistribution(distribution)
                .recentRequests(recent)
                .build();
    }
}
