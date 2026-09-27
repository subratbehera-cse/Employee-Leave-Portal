package com.leavemanagement.controller;

import com.leavemanagement.dto.request.AdminEmployeeUpdateRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.service.AdminEmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
@Tag(name = "Admin - Employees")
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    @GetMapping
    public List<UserResponse> getAll() {
        return adminEmployeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return adminEmployeeService.getEmployeeById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminEmployeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody AdminEmployeeUpdateRequest request) {
        return adminEmployeeService.updateEmployee(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminEmployeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
