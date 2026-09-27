package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveRequestCreateRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.dto.response.LeaveRequestResponse;

import java.util.List;

public interface LeaveService {
    LeaveRequestResponse applyLeave(Long userId, LeaveRequestCreateRequest request);
    List<LeaveRequestResponse> getMyLeaves(Long userId);
    LeaveRequestResponse getLeaveById(Long userId, String role, Long leaveId);
    LeaveRequestResponse cancelLeave(Long userId, Long leaveId);
    List<LeaveBalanceResponse> getMyBalances(Long userId);
}
