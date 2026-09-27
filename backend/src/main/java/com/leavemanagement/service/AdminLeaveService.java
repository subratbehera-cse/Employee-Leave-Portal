package com.leavemanagement.service;

import com.leavemanagement.dto.response.LeaveRequestResponse;

import java.util.List;

public interface AdminLeaveService {
    List<LeaveRequestResponse> getAllLeaveRequests();
    LeaveRequestResponse getLeaveRequestById(Long id);
}
