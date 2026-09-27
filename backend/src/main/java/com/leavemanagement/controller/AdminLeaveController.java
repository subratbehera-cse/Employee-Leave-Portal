package com.leavemanagement.controller;

import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.service.AdminLeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/leaves")
@RequiredArgsConstructor
@Tag(name = "Admin - Leave Requests")
public class AdminLeaveController {

    private final AdminLeaveService adminLeaveService;

    @GetMapping
    public List<LeaveRequestResponse> getAll() {
        return adminLeaveService.getAllLeaveRequests();
    }

    @GetMapping("/{id}")
    public LeaveRequestResponse getById(@PathVariable Long id) {
        return adminLeaveService.getLeaveRequestById(id);
    }
}
