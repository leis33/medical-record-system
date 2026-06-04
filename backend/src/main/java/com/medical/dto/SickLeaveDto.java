package com.medical.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SickLeaveDto {
    private Long id;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @Min(value = 1, message = "Number of days must be at least 1")
    private int numberOfDays;

    private Long examinationId;
}
