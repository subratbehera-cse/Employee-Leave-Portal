package com.leavemanagement.dto.response;

import com.leavemanagement.enums.LeaveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String employeeCode;
    private Long leaveTypeId;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double numberOfDays;
    private String reason;
    private LeaveStatus status;
    private String managerComment;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewedByName;
}
