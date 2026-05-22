package nbu.springProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PatientDto {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;

        @NotBlank(message = "Personal identification number is required")
        @Size(min = 10, max = 10, message = "PIN must be exactly 10 digits")
        @Pattern(regexp = "\\d{10}", message = "PIN must contain only digits")
        private String personalIdentificationNumber;

        private Long personalDoctorId;

        private boolean hasHealthInsurance = false;

        private Long userId;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String personalIdentificationNumber;
        private Long personalDoctorId;
        private String personalDoctorName;
        private boolean hasHealthInsurance;
        private Long userId;
        private String username;
    }
}