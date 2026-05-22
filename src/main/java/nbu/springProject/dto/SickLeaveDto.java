package nbu.springProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

public class SickLeaveDto {

    @Data
    public static class Request {
        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "Number of days is required")
        @Min(value = 1, message = "Number of days must be at least 1")
        private Integer numberOfDays;

        @NotNull(message = "Examination ID is required")
        private Long examinationId;
    }

    @Data
    public static class Response {
        private Long id;
        private LocalDate startDate;
        private Integer numberOfDays;
        private Long examinationId;
        private LocalDate examinationDate;
        private String patientName;
        private String doctorName;
        private String diagnosisName;
    }
}
