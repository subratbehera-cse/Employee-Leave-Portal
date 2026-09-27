package com.leavemanagement.mapper;

import com.leavemanagement.dto.response.LeaveBalanceResponse;
import com.leavemanagement.entity.LeaveBalance;
import org.springframework.stereotype.Component;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse toResponse(LeaveBalance balance) {
        if (balance == null) return null;
        return LeaveBalanceResponse.builder()
                .id(balance.getId())
                .userId(balance.getUser().getId())
                .userName(balance.getUser().getFirstName() + " " + balance.getUser().getLastName())
                .leaveTypeId(balance.getLeaveType().getId())
                .leaveTypeName(balance.getLeaveType().getName())
                .totalDays(balance.getTotalDays())
                .usedDays(balance.getUsedDays())
                .remainingDays(balance.getRemainingDays())
                .year(balance.getYear())
                .build();
    }
}
