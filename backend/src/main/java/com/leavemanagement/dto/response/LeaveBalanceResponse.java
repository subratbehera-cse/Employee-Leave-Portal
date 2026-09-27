package com.leavemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private Double totalDays;
    private Double usedDays;
    private Double remainingDays;
    private Integer year;
}
