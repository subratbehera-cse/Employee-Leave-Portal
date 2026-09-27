package com.leavemanagement.service.impl;

import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.exception.ResourceNotFoundException;
import com.leavemanagement.mapper.LeaveRequestMapper;
import com.leavemanagement.repository.LeaveRequestRepository;
import com.leavemanagement.service.AdminLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminLeaveServiceImpl implements AdminLeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    @Override
    public List<LeaveRequestResponse> getAllLeaveRequests() {
        return leaveRequestRepository.findAllByOrderByAppliedAtDesc().stream()
                .map(leaveRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestResponse getLeaveRequestById(Long id) {
        return leaveRequestMapper.toResponse(leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id)));
    }
}
