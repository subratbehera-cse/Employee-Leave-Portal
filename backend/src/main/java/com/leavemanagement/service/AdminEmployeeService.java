package com.leavemanagement.service;

import com.leavemanagement.dto.request.AdminEmployeeUpdateRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.UserResponse;

import java.util.List;

public interface AdminEmployeeService {
    List<UserResponse> getAllEmployees();
    UserResponse getEmployeeById(Long id);
    UserResponse createEmployee(RegisterRequest request);
    UserResponse updateEmployee(Long id, AdminEmployeeUpdateRequest request);
    void deleteEmployee(Long id);
}
