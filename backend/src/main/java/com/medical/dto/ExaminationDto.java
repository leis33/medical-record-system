package com.medical.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ExaminationDto {
    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Doctor is required")
    private Long doctorId;
    private String doctorName;

    @NotNull(message = "Patient is required")
    private Long patientId;
    private String patientName;

    private Long diagnosisId;
    private String diagnosisDescription;

    @NotBlank(message = "Prescribed treatment is required")
    private String prescribedTreatment;

    @NotNull
    @DecimalMin(value = "0.0", message = "Cost must be non-negative")
    private BigDecimal cost;

    private boolean paidByNHIF;
    private List<SickLeaveDto> sickLeaves;
}
