package com.leavemanagement.service;

import com.leavemanagement.dto.request.LoginRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
