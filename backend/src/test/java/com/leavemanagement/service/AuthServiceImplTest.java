package com.leavemanagement.service;

import com.leavemanagement.dto.request.LoginRequest;
import com.leavemanagement.dto.request.RegisterRequest;
import com.leavemanagement.dto.response.AuthResponse;
import com.leavemanagement.entity.User;
import com.leavemanagement.enums.EmployeeStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.mapper.UserMapper;
import com.leavemanagement.repository.DepartmentRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.security.JwtUtil;
import com.leavemanagement.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("jane.doe@company.com");
        registerRequest.setPassword("Password123");
        registerRequest.setRole(Role.EMPLOYEE);
        registerRequest.setJoiningDate(LocalDate.now());
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldCreateUserAndReturnToken_whenEmailIsNew() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.existsByEmployeeCode(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(1L, registerRequest.getEmail(), "EMPLOYEE")).thenReturn("mock-jwt-token");
        when(userMapper.toResponse(any(User.class))).thenReturn(
                com.leavemanagement.dto.response.UserResponse.builder()
                        .id(1L).email(registerRequest.getEmail()).role(Role.EMPLOYEE).build());

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(registerRequest.getEmail(), response.getUser().getEmail());

        verify(passwordEncoder).encode("Password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_shouldThrow_whenAccountIsInactive() {
        User inactiveUser = User.builder()
                .id(2L)
                .email("inactive@company.com")
                .status(EmployeeStatus.INACTIVE)
                .role(Role.EMPLOYEE)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("inactive@company.com");
        loginRequest.setPassword("Password123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));

        assertThrows(com.leavemanagement.exception.BadRequestException.class, () -> authService.login(loginRequest));
    }
}
