package com.leavemanagement.service;

import com.leavemanagement.dto.response.AdminDashboardResponse;
import com.leavemanagement.dto.response.EmployeeDashboardResponse;
import com.leavemanagement.dto.response.ManagerDashboardResponse;

public interface DashboardService {
    EmployeeDashboardResponse getEmployeeDashboard(Long userId);
    ManagerDashboardResponse getManagerDashboard(Long managerId);
    AdminDashboardResponse getAdminDashboard();
}
