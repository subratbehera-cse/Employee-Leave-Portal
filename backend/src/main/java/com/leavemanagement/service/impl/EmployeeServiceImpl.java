package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.UpdateProfileRequest;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.entity.User;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Override
    public UserResponse getMyProfile(Long userId) {
        User user = findUser(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user = userRepository.save(user);

        auditLogService.log(userId, "UPDATE_PROFILE", "User", userId, "Profile updated by employee");

        return userMapper.toResponse(user);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
