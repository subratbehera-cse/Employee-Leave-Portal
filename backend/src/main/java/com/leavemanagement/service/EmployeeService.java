package com.leavemanagement.service;

import com.leavemanagement.dto.request.UpdateProfileRequest;
import com.leavemanagement.dto.response.UserResponse;

public interface EmployeeService {
    UserResponse getMyProfile(Long userId);
    UserResponse updateMyProfile(Long userId, UpdateProfileRequest request);
}
