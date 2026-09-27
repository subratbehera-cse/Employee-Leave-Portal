package com.leavemanagement.service.impl;

import com.leavemanagement.dto.request.LeaveTypeRequest;
import com.leavemanagement.dto.response.LeaveTypeResponse;
import com.leavemanagement.entity.LeaveType;
import com.leavemanagement.exception.DuplicateResourceException;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.LeaveTypeMapper;
import com.leavemanagement.repository.LeaveTypeRepository;
import com.leavemanagement.service.AuditLogService;
import com.leavemanagement.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;
    private final AuditLogService auditLogService;

    @Override
    public List<LeaveTypeResponse> getAll() {
        return leaveTypeRepository.findAll().stream()
                .map(leaveTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveTypeResponse getById(Long id) {
        return leaveTypeMapper.toResponse(findLeaveType(id));
    }

    @Override
    @Transactional
    public LeaveTypeResponse create(LeaveTypeRequest request) {
        if (leaveTypeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A leave type with this name already exists");
        }
        LeaveType leaveType = LeaveType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .defaultDays(request.getDefaultDays())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        leaveType = leaveTypeRepository.save(leaveType);

        auditLogService.log(null, "CREATE_LEAVE_TYPE", "LeaveType", leaveType.getId(),
                "Leave type created: " + leaveType.getName());

        return leaveTypeMapper.toResponse(leaveType);
    }

    @Override
    @Transactional
    public LeaveTypeResponse update(Long id, LeaveTypeRequest request) {
        LeaveType leaveType = findLeaveType(id);

        if (!leaveType.getName().equalsIgnoreCase(request.getName())
                && leaveTypeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("A leave type with this name already exists");
        }

        leaveType.setName(request.getName());
        leaveType.setDescription(request.getDescription());
        leaveType.setDefaultDays(request.getDefaultDays());
        leaveType.setIsActive(request.getIsActive() != null ? request.getIsActive() : leaveType.getIsActive());
        leaveType = leaveTypeRepository.save(leaveType);

        auditLogService.log(null, "UPDATE_LEAVE_TYPE", "LeaveType", id, "Leave type updated: " + leaveType.getName());

        return leaveTypeMapper.toResponse(leaveType);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        LeaveType leaveType = findLeaveType(id);
        leaveType.setIsActive(false);
        leaveTypeRepository.save(leaveType);
        auditLogService.log(null, "DEACTIVATE_LEAVE_TYPE", "LeaveType", id, "Leave type deactivated: " + leaveType.getName());
    }

    private LeaveType findLeaveType(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));
    }
}
