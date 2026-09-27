package com.leavemanagement.mapper;

import com.leavemanagement.dto.response.UserResponse;
import com.leavemanagement.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .employeeCode(user.getEmployeeCode())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                .managerId(user.getManager() != null ? user.getManager().getId() : null)
                .managerName(user.getManager() != null
                        ? user.getManager().getFirstName() + " " + user.getManager().getLastName()
                        : null)
                .joiningDate(user.getJoiningDate())
                .status(user.getStatus())
                .build();
    }
}
