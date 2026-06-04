package com.medical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiagnosisDto {
    private Long id;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    private int examinationCount;
}
