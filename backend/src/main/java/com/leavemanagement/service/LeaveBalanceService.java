package com.leavemanagement.service;

import com.leavemanagement.dto.request.LeaveBalanceRequest;
import com.leavemanagement.dto.response.LeaveBalanceResponse;

import java.util.List;

public interface LeaveBalanceService {
    List<LeaveBalanceResponse> getAll();
    LeaveBalanceResponse create(LeaveBalanceRequest request);
    LeaveBalanceResponse update(Long id, LeaveBalanceRequest request);
    void delete(Long id);
}
