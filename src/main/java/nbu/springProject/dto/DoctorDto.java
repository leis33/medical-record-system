package nbu.springProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class DoctorDto {

    @Data
    public static class Request {
        @NotBlank(message = "Identification number is required")
        @Size(min = 5, max = 20, message = "ID number must be 5-20 characters")
        private String identificationNumber;

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100)
        private String name;

        @NotBlank(message = "Specialty is required")
        private String specialty;

        private boolean canBePersonalDoctor = false;

        private Long userId;
    }

    @Data
    public static class Response {
        private Long id;
        private String identificationNumber;
        private String name;
        private String specialty;
        private boolean canBePersonalDoctor;
        private Long userId;
        private String username;
        private int patientCount;
    }
}
