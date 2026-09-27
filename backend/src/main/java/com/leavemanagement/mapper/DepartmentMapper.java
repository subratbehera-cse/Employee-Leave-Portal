package com.leavemanagement.mapper;

import com.leavemanagement.dto.response.DepartmentResponse;
import com.leavemanagement.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department, long employeeCount) {
        if (department == null) return null;
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .employeeCount(employeeCount)
                .build();
    }
}
