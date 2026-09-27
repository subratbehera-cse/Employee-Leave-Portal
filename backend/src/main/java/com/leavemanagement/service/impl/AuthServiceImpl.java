package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.LoginRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.AuthResponse;
import com.leavemanagement.entity.Department;
import com.leavemanagement.entity.User;
import com.leavemanagement.enums.EmployeeStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.BadRequestException;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.DepartmentRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.security.JwtUtil;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));
        }

        User manager = null;
        if (request.getManagerId() != null) {
            manager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + request.getManagerId()));
        }

        Role role = request.getRole() != null ? request.getRole() : Role.EMPLOYEE;

        User user = User.builder()
                .employeeCode(generateEmployeeCode(role))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .department(department)
                .manager(manager)
                .joiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now())
                .status(EmployeeStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        auditLogService.log(user.getId(), "REGISTER", "User", user.getId(),
                "New account registered: " + user.getEmail());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (user.getStatus() == EmployeeStatus.INACTIVE) {
            throw new BadRequestException("This account has been deactivated. Please contact an administrator.");
        }

        auditLogService.log(user.getId(), "LOGIN", "User", user.getId(), "User logged in: " + user.getEmail());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userMapper.toResponse(user))
                .build();
    }

    private String generateEmployeeCode(Role role) {
        String prefix = switch (role) {
            case ADMIN -> "ADM";
            case MANAGER -> "MGR";
            default -> "EMP";
        };
        String code;
        AtomicInteger suffix = new AtomicInteger((int) (userRepository.count() + 1));
        do {
            code = prefix + String.format("%03d", suffix.getAndIncrement());
        } while (userRepository.existsByEmployeeCode(code));
        return code;
    }
}
