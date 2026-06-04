package com.medical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PatientDto {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Personal identification number is required")
    @Pattern(regexp = "\\d{10}", message = "Personal identification number must be exactly 10 digits")
    private String personalIdentificationNumber;

    private Long personalDoctorId;
    private String personalDoctorName;
    private boolean hasHealthInsurance;
    private int examinationCount;
}
