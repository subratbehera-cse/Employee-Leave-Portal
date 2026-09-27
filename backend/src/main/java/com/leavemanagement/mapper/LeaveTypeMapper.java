package com.leavemanagement.mapper;

import com.leavemanagement.dto.response.LeaveTypeResponse;
import com.leavemanagement.entity.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {

    public LeaveTypeResponse toResponse(LeaveType leaveType) {
        if (leaveType == null) return null;
        return LeaveTypeResponse.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .description(leaveType.getDescription())
                .defaultDays(leaveType.getDefaultDays())
                .isActive(leaveType.getIsActive())
                .build();
    }
}
