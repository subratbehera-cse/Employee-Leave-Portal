package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveReviewRequest;
import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.dto.response.UserResponse;

import java.util.List;

public interface ManagerService {
    List<UserResponse> getTeamMembers(Long managerId);
    List<LeaveRequestResponse> getTeamLeaveRequests(Long managerId);
    LeaveRequestResponse approveLeave(Long managerId, Long leaveId, LeaveReviewRequest request);
    LeaveRequestResponse rejectLeave(Long managerId, Long leaveId, LeaveReviewRequest request);
}
