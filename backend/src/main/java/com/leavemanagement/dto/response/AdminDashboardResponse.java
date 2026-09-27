package com.leavemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalEmployees;
    private long totalDepartments;
    private long pendingRequests;
    private long approvedRequests;
    private long rejectedRequests;
    private Map<String, Long> leaveTypeDistribution;
    private List<LeaveRequestResponse> recentRequests;
}
