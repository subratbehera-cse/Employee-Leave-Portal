package com.leavemanagement.dto.response;

import com.leavemanagement.enums.EmployeeStatus;
import com.leavemanagement.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerName;
    private LocalDate joiningDate;
    private EmployeeStatus status;
}
