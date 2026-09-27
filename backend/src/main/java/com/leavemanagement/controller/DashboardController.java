package com.leavemanagement.controller;

import com.leavemanagement.dto.response.AdminDashboardResponse;
import com.leavemanagement.dto.response.EmployeeDashboardResponse;
import com.leavemanagement.security.CustomUserDetails;
import com.leavemanagement.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/employees/dashboard")
    public EmployeeDashboardResponse getEmployeeDashboard(@AuthenticationPrincipal CustomUserDetails principal) {
        return dashboardService.getEmployeeDashboard(principal.getId());
    }

    @GetMapping("/api/admin/dashboard")
    public AdminDashboardResponse getAdminDashboard() {
        return dashboardService.getAdminDashboard();
    }
}
