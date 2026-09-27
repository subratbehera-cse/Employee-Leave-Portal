package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.AdminEmployeeUpdateRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.AuthResponse;
import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.entity.Department;
import com.leavemanagement.entity.User;
import com.leavemanagement.exception.BadRequestException;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.DepartmentRepository;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AdminEmployeeService;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEmployeeServiceImpl implements AdminEmployeeService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final AuditLogService auditLogService;

    @Override
    public List<UserResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getEmployeeById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Override
    @Transactional
    public UserResponse createEmployee(RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return response.getUser();
    }

    @Override
    @Transactional
    public UserResponse updateEmployee(Long id, AdminEmployeeUpdateRequest request) {
        User user = findUser(id);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Another account already uses this email");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        }

        User manager = null;
        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(id)) {
                throw new BadRequestException("An employee cannot be their own manager");
            }
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setDepartment(department);
        user.setManager(manager);
        user.setJoiningDate(request.getJoiningDate());
        user.setStatus(request.getStatus());

        user = userRepository.save(user);

        auditLogService.log(id, "UPDATE_EMPLOYEE", "User", id, "Employee record updated by admin");

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        User user = findUser(id);

        if (!leaveRequestRepository.findByUserIdOrderByAppliedAtDesc(id).isEmpty()) {
            // Preserve leave history for audit purposes; deactivate instead of hard-deleting.
            user.setStatus(com.leavemanagement.enums.EmployeeStatus.INACTIVE);
            userRepository.save(user);
            auditLogService.log(id, "DEACTIVATE_EMPLOYEE", "User", id,
                    "Employee deactivated (has leave history, so record was preserved)");
            return;
        }

        userRepository.delete(user);
        auditLogService.log(id, "DELETE_EMPLOYEE", "User", id, "Employee record deleted");
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }
}
