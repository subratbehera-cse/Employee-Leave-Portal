package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveRequestCreateRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.security.CustomUserDetails;
import com.leavemanagement.service.LeaveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@Tag(name = "Leave")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<LeaveRequestResponse> applyLeave(@AuthenticationPrincipal CustomUserDetails principal,
                                                             @Valid @RequestBody LeaveRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.applyLeave(principal.getId(), request));
    }

    @GetMapping("/my")
    public List<LeaveRequestResponse> getMyLeaves(@AuthenticationPrincipal CustomUserDetails principal) {
        return leaveService.getMyLeaves(principal.getId());
    }

    @GetMapping("/balance")
    public List<LeaveBalanceResponse> getMyBalance(@AuthenticationPrincipal CustomUserDetails principal) {
        return leaveService.getMyBalances(principal.getId());
    }

    @GetMapping("/{id}")
    public LeaveRequestResponse getLeaveById(@AuthenticationPrincipal CustomUserDetails principal,
                                              @PathVariable Long id) {
        return leaveService.getLeaveById(principal.getId(), principal.getRole(), id);
    }

    @PutMapping("/{id}/cancel")
    public LeaveRequestResponse cancelLeave(@AuthenticationPrincipal CustomUserDetails principal,
                                             @PathVariable Long id) {
        return leaveService.cancelLeave(principal.getId(), id);
    }
}
