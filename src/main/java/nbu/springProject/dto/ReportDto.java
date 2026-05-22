package nbu.springProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

public class ReportDto {

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DiagnosisCount {
        private Long diagnosisId;
        private String diagnosisCode;
        private String diagnosisName;
        private Long count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DoctorPatientCount {
        private Long doctorId;
        private String doctorName;
        private String specialty;
        private Long patientCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DoctorVisitCount {
        private Long doctorId;
        private String doctorName;
        private String specialty;
        private Long visitCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DoctorRevenue {
        private Long doctorId;
        private String doctorName;
        private BigDecimal totalRevenue;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class DoctorSickLeaveCount {
        private Long doctorId;
        private String doctorName;
        private Long sickLeaveCount;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class MonthSickLeaveCount {
        private Integer year;
        private Integer month;
        private String monthName;
        private Long count;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class TotalRevenue {
        private BigDecimal totalPaidByPatients;
        private BigDecimal totalPaidByNhif;
    }
}
