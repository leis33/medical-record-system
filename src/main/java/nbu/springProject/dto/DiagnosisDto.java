package nbu.springProject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class DiagnosisDto {

    @Data
    public static class Request {
        @NotBlank(message = "Diagnosis code is required")
        @Size(min = 2, max = 20)
        private String code;

        @NotBlank(message = "Diagnosis name is required")
        @Size(min = 2, max = 200)
        private String name;

        private String description;
    }

    @Data
    public static class Response {
        private Long id;
        private String code;
        private String name;
        private String description;
        private int examinationCount;
    }
}
