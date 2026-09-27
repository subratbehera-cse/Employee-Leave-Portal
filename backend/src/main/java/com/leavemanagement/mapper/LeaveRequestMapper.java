package com.leavemanagement.mapper;

import com.leavemanagement.dto.response.LeaveRequestResponse;
import com.leavemanagement.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequestResponse toResponse(LeaveRequest request) {
        if (request == null) return null;
        return LeaveRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUser().getFirstName() + " " + request.getUser().getLastName())
                .employeeCode(request.getUser().getEmployeeCode())
                .leaveTypeId(request.getLeaveType().getId())
                .leaveTypeName(request.getLeaveType().getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .numberOfDays(request.getNumberOfDays())
                .reason(request.getReason())
                .status(request.getStatus())
                .managerComment(request.getManagerComment())
                .appliedAt(request.getAppliedAt())
                .reviewedAt(request.getReviewedAt())
                .reviewedByName(request.getReviewedBy() != null
                        ? request.getReviewedBy().getFirstName() + " " + request.getReviewedBy().getLastName()
                        : null)
                .build();
    }
}
