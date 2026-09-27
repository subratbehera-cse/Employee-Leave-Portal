package com.leavemanagement.controller;

import com.leavemanagement.dto.request.LeaveTypeRequest;
import com.leavemanagement.dto.response.LeaveTypeResponse;
import com.leavemanagement.service.LeaveTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/leave-types")
@RequiredArgsConstructor
@Tag(name = "Admin - Leave Types")
public class AdminLeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @GetMapping
    public List<LeaveTypeResponse> getAll() {
        return leaveTypeService.getAll();
    }

    @GetMapping("/{id}")
    public LeaveTypeResponse getById(@PathVariable Long id) {
        return leaveTypeService.getById(id);
    }

    @PostMapping
    public ResponseEntity<LeaveTypeResponse> create(@Valid @RequestBody LeaveTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveTypeService.create(request));
    }

    @PutMapping("/{id}")
    public LeaveTypeResponse update(@PathVariable Long id, @Valid @RequestBody LeaveTypeRequest request) {
        return leaveTypeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
