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
public class EmployeeDashboardResponse {
    private List<LeaveBalanceResponse> leaveBalances;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private LeaveRequestResponse upcomingLeave;
    private List<LeaveRequestResponse> recentRequests;
}
