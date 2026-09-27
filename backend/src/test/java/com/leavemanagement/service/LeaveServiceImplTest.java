package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveRequestCreateRequest;
import com.leavemanagement.entity.*;
import com.leavemanagement.enums.LeaveStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.ForbiddenException;
import com.leavemanagement.exception.InsufficientLeaveBalanceException;
import com.leavemanagement.exception.InvalidLeaveRequestException;
import com.leavemanagement.mapper.LeaveBalanceMapper;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.repository.LeaveBalanceRepository;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.repository.LeaveTypeRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock private LeaveRequestRepository leaveRequestRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private LeaveRequestMapper leaveRequestMapper;
    @Mock private LeaveBalanceMapper leaveBalanceMapper;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private User employee;
    private LeaveType leaveType;

    @BeforeEach
    void setUp() {
        employee = User.builder().id(10L).firstName("Emma").lastName("Employee").role(Role.EMPLOYEE).build();
        leaveType = LeaveType.builder().id(1L).name("Annual Leave").isActive(true).defaultDays(20).build();
    }

    @Test
    void applyLeave_shouldThrow_whenStartDateAfterEndDate() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(5));
        request.setEndDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));

        assertThrows(InvalidLeaveRequestException.class, () -> leaveService.applyLeave(10L, request));
    }

    @Test
    void applyLeave_shouldThrow_whenOverlappingRequestExists() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        when(userRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlapping(eq(10L), any(), any()))
                .thenReturn(List.of(new LeaveRequest()));

        assertThrows(InvalidLeaveRequestException.class, () -> leaveService.applyLeave(10L, request));
    }

    @Test
    void applyLeave_shouldThrow_whenBalanceInsufficient() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(10)); // 10 days requested

        LeaveBalance balance = LeaveBalance.builder()
                .id(1L).user(employee).leaveType(leaveType)
                .totalDays(5.0).usedDays(0.0).remainingDays(5.0)
                .year(request.getStartDate().getYear())
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlapping(eq(10L), any(), any())).thenReturn(List.of());
        when(leaveBalanceRepository.findByUserIdAndLeaveTypeIdAndYear(eq(10L), eq(1L), any()))
                .thenReturn(Optional.of(balance));

        assertThrows(InsufficientLeaveBalanceException.class, () -> leaveService.applyLeave(10L, request));
    }

    @Test
    void applyLeave_shouldSucceed_whenValid() {
        LeaveRequestCreateRequest request = new LeaveRequestCreateRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3)); // 3 days

        LeaveBalance balance = LeaveBalance.builder()
                .id(1L).user(employee).leaveType(leaveType)
                .totalDays(20.0).usedDays(0.0).remainingDays(20.0)
                .year(request.getStartDate().getYear())
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveRequestRepository.findOverlapping(eq(10L), any(), any())).thenReturn(List.of());
        when(leaveBalanceRepository.findByUserIdAndLeaveTypeIdAndYear(eq(10L), eq(1L), any()))
                .thenReturn(Optional.of(balance));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(inv -> {
            LeaveRequest lr = inv.getArgument(0);
            lr.setId(100L);
            return lr;
        });

        leaveService.applyLeave(10L, request);

        verify(leaveRequestRepository).save(argThat(lr ->
                lr.getStatus() == LeaveStatus.PENDING && lr.getNumberOfDays() == 3.0));
    }

    @Test
    void cancelLeave_shouldThrow_whenNotOwner() {
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .id(5L).user(employee).status(LeaveStatus.PENDING).build();

        when(leaveRequestRepository.findById(5L)).thenReturn(Optional.of(leaveRequest));

        assertThrows(ForbiddenException.class, () -> leaveService.cancelLeave(999L, 5L));
    }
}
