package com.leavemanagement.controller;

import com.leavemanagement.dto.request.UpdateProfileRequest;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.security.CustomUserDetails;
import com.leavemanagement.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/me")
    public UserResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails principal) {
        return employeeService.getMyProfile(principal.getId());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(@AuthenticationPrincipal CustomUserDetails principal,
                                         @Valid @RequestBody UpdateProfileRequest request) {
        return employeeService.updateMyProfile(principal.getId(), request);
    }
}
