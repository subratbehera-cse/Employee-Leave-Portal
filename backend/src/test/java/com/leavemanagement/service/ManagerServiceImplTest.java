package com.leavemanagement.service;

import com.leavemanagement.entity.*;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.ForbiddenException;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.impl.ManagerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private LeaveRequestRepository leaveRequestRepository;
    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private UserMapper userMapper;
    @Mock private LeaveRequestMapper leaveRequestMapper;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private User manager;

    @BeforeEach
    void setUp() {
        manager = User.builder().id(1L).firstName("Morgan").lastName("Manager").role(Role.MANAGER).build();
    }

    @Test
    void approveLeave_shouldThrow_whenManagerTriesToApproveOwnLeave() {
        LeaveRequest ownRequest = LeaveRequest.builder()
                .id(50L).user(manager).status(LeaveStatus.PENDING).build();

        when(leaveRequestRepository.findById(50L)).thenReturn(Optional.of(ownRequest));

        assertThrows(ForbiddenException.class, () -> managerService.approveLeave(1L, 50L, null));
    }

    @Test
    void approveLeave_shouldThrow_whenEmployeeNotOnManagerTeam() {
        User otherManager = User.builder().id(2L).role(Role.MANAGER).build();
        User employee = User.builder().id(20L).manager(otherManager).role(Role.EMPLOYEE).build();
        LeaveRequest request = LeaveRequest.builder().id(51L).user(employee).status(LeaveStatus.PENDING).build();

        when(leaveRequestRepository.findById(51L)).thenReturn(Optional.of(request));

        assertThrows(ForbiddenException.class, () -> managerService.approveLeave(1L, 51L, null));
    }

    @Test
    void approveLeave_shouldUpdateBalance_whenValidTeamRequest() {
        LeaveType leaveType = LeaveType.builder().id(1L).name("Annual Leave").isActive(true).build();
        User employee = User.builder().id(20L).firstName("Emma").lastName("Employee").manager(manager).role(Role.EMPLOYEE).build();
        LeaveRequest request = LeaveRequest.builder()
                .id(51L).user(employee).leaveType(leaveType).status(LeaveStatus.PENDING)
                .startDate(LocalDate.now().plusDays(1)).endDate(LocalDate.now().plusDays(3))
                .numberOfDays(3.0).build();

        LeaveBalance balance = LeaveBalance.builder()
                .id(1L).user(employee).leaveType(leaveType)
                .totalDays(20.0).usedDays(0.0).remainingDays(20.0)
                .year(request.getStartDate().getYear()).build();

        when(leaveRequestRepository.findById(51L)).thenReturn(Optional.of(request));
        when(leaveBalanceRepository.findByUserIdAndLeaveTypeIdAndYear(any(), any(), any()))
                .thenReturn(Optional.of(balance));
        when(userRepository.findById(1L)).thenReturn(Optional.of(manager));

        managerService.approveLeave(1L, 51L, null);

        assertEquals(3.0, balance.getUsedDays());
        assertEquals(17.0, balance.getRemainingDays());
        assertEquals(LeaveStatus.APPROVED, request.getStatus());
    }
}
