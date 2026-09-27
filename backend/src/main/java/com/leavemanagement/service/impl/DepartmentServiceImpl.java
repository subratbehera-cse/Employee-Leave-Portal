package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.DepartmentRequest;
import com.leavemanagement.dto.response.DepartmentResponse;
import com.leavemanagement.entity.Department;
import com.leavemanagement.exception.BadRequestException;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.DepartmentMapper;
import com.leavemanagement.repository.DepartmentRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final DepartmentMapper departmentMapper;
    private final AuditLogService auditLogService;

    @Override
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(d -> departmentMapper.toResponse(d, userRepository.countByDepartmentId(d.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse getById(Long id) {
        Department department = findDepartment(id);
        return departmentMapper.toResponse(department, userRepository.countByDepartmentId(id));
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A department with this name already exists");
        }
        Department department = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        department = departmentRepository.save(department);

        auditLogService.log(null, "CREATE_DEPARTMENT", "Department", department.getId(),
                "Department created: " + department.getName());

        return departmentMapper.toResponse(department, 0);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);

        if (!department.getName().equalsIgnoreCase(request.getName())
                && departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A department with this name already exists");
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department = departmentRepository.save(department);

        auditLogService.log(null, "UPDATE_DEPARTMENT", "Department", id, "Department updated: " + department.getName());

        return departmentMapper.toResponse(department, userRepository.countByDepartmentId(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department department = findDepartment(id);

        if (userRepository.countByDepartmentId(id) > 0) {
            throw new BadRequestException("Cannot delete a department that still has employees assigned to it");
        }

        departmentRepository.delete(department);
        auditLogService.log(null, "DELETE_DEPARTMENT", "Department", id, "Department deleted: " + department.getName());
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }
}
