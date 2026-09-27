package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveTypeRequest;
import com.leavemanagement.dto.response.LeaveTypeResponse;

import java.util.List;

public interface LeaveTypeService {
    List<LeaveTypeResponse> getAll();
    LeaveTypeResponse getById(Long id);
    LeaveTypeResponse create(LeaveTypeRequest request);
    LeaveTypeResponse update(Long id, LeaveTypeRequest request);
    void delete(Long id);
}
