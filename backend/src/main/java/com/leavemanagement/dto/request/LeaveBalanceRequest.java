package com.leavemanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveBalanceRequest {

    @NotNull(message = "User is required")
    private Long userId;

    @NotNull(message = "Leave type is required")
    private Long leaveTypeId;

    @NotNull(message = "Total days is required")
    @PositiveOrZero(message = "Total days cannot be negative")
    private Double totalDays;

    @NotNull(message = "Year is required")
    private Integer year;
}
