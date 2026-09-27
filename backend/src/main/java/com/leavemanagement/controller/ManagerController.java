package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveReviewRequest;
import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.dto.response.ManagerDashboardResponse;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.security.CustomUserDetails;
import com.leavemanagement.service.DashboardService;
import com.leavemanagement.service.ManagerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Tag(name = "Manager")
public class ManagerController {

    private final ManagerService managerService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public ManagerDashboardResponse getDashboard(@AuthenticationPrincipal CustomUserDetails principal) {
        return dashboardService.getManagerDashboard(principal.getId());
    }

    @GetMapping("/team")
    public List<UserResponse> getTeam(@AuthenticationPrincipal CustomUserDetails principal) {
        return managerService.getTeamMembers(principal.getId());
    }

    @GetMapping("/leaves")
    public List<LeaveRequestResponse> getTeamLeaves(@AuthenticationPrincipal CustomUserDetails principal) {
        return managerService.getTeamLeaveRequests(principal.getId());
    }

    @PutMapping("/leaves/{id}/approve")
    public LeaveRequestResponse approveLeave(@AuthenticationPrincipal CustomUserDetails principal,
                                              @PathVariable Long id,
                                              @RequestBody(required = false) LeaveReviewRequest request) {
        return managerService.approveLeave(principal.getId(), id, request);
    }

    @PutMapping("/leaves/{id}/reject")
    public LeaveRequestResponse rejectLeave(@AuthenticationPrincipal CustomUserDetails principal,
                                             @PathVariable Long id,
                                             @RequestBody(required = false) LeaveReviewRequest request) {
        return managerService.rejectLeave(principal.getId(), id, request);
    }
}
