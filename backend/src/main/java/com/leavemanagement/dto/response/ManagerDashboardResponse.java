package com.leavemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDashboardResponse {
    private long teamSize;
    private long pendingApprovals;
    private long approvedRequests;
    private long rejectedRequests;
    private List<LeaveRequestResponse> teamLeaveRequests;
}
