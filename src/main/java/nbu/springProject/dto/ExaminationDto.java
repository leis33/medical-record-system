package nbu.springProject.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExaminationDto {

    @Data
    public static class Request {
        @NotNull(message = "Examination date is required")
        private LocalDate examinationDate;

        @NotNull(message = "Doctor ID is required")
        private Long doctorId;

        @NotNull(message = "Patient ID is required")
        private Long patientId;

        private Long diagnosisId;

        private String prescribedTreatment;

        @NotNull(message = "Cost is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be positive")
        private BigDecimal cost;
    }

    @Data
    public static class Response {
        private Long id;
        private LocalDate examinationDate;
        private Long doctorId;
        private String doctorName;
        private Long patientId;
        private String patientName;
        private Long diagnosisId;
        private String diagnosisName;
        private String diagnosisCode;
        private String prescribedTreatment;
        private BigDecimal cost;
        private boolean paidByNhif;
        private SickLeaveDto.Response sickLeave;
    }
}
