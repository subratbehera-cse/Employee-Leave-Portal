package com.leavemanagement.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    private String name;

    private String description;

    @NotNull(message = "Default days is required")
    @Min(value = 0, message = "Default days cannot be negative")
    private Integer defaultDays;

    private Boolean isActive = true;
}
