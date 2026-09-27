package com.leavemanagement.dto.request;

import com.leavemanagement.enums.EmployeeStatus;
import com.leavemanagement.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminEmployeeUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @NotNull(message = "Role is required")
    private Role role;

    private Long departmentId;

    private Long managerId;

    private LocalDate joiningDate;

    @NotNull(message = "Status is required")
    private EmployeeStatus status;
}
