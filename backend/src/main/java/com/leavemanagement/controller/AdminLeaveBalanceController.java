package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveBalanceRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.service.LeaveBalanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/leave-balances")
@RequiredArgsConstructor
@Tag(name = "Admin - Leave Balances")
public class AdminLeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping
    public List<LeaveBalanceResponse> getAll() {
        return leaveBalanceService.getAll();
    }

    @PostMapping
    public ResponseEntity<LeaveBalanceResponse> create(@Valid @RequestBody LeaveBalanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveBalanceService.create(request));
    }

    @PutMapping("/{id}")
    public LeaveBalanceResponse update(@PathVariable Long id, @Valid @RequestBody LeaveBalanceRequest request) {
        return leaveBalanceService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveBalanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
