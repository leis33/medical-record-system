package com.medical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DoctorDto {
    private Long id;

    @NotBlank(message = "Identification number is required")
    private String identificationNumber;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    private boolean canBePersonalDoctor;
    private int patientCount;
    private int examinationCount;
}
