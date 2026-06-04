package com.medical.controller;

import com.medical.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/patients-by-diagnosis/{diagnosisId}")
    public ResponseEntity<?> patientsByDiagnosis(@PathVariable Long diagnosisId) {
        return ResponseEntity.ok(reportService.patientsByDiagnosis(diagnosisId));
    }

    @GetMapping("/most-common-diagnoses")
    public ResponseEntity<List<Map<String, Object>>> mostCommonDiagnoses() {
        return ResponseEntity.ok(reportService.mostCommonDiagnoses());
    }

    @GetMapping("/patients-by-gp/{doctorId}")
    public ResponseEntity<?> patientsByGP(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reportService.patientsByGP(doctorId));
    }

    @GetMapping("/total-paid-by-patients")
    public ResponseEntity<?> totalPaidByPatients() {
        return ResponseEntity.ok(Map.of("total", reportService.totalPaidByPatients()));
    }

    @GetMapping("/paid-by-patients-per-doctor")
    public ResponseEntity<List<Map<String, Object>>> paidByPatientsByDoctor() {
        return ResponseEntity.ok(reportService.paidByPatientsByDoctor());
    }

    @GetMapping("/patient-count-by-gp")
    public ResponseEntity<List<Map<String, Object>>> patientCountByGP() {
        return ResponseEntity.ok(reportService.patientCountByGP());
    }

    @GetMapping("/visit-count-by-doctor")
    public ResponseEntity<List<Map<String, Object>>> visitCountByDoctor() {
        return ResponseEntity.ok(reportService.visitCountByDoctor());
    }

    @GetMapping("/month-most-sick-leaves")
    public ResponseEntity<List<Map<String, Object>>> monthWithMostSickLeaves() {
        return ResponseEntity.ok(reportService.monthWithMostSickLeaves());
    }

    @GetMapping("/doctors-most-sick-leaves")
    public ResponseEntity<List<Map<String, Object>>> doctorsWithMostSickLeaves() {
        return ResponseEntity.ok(reportService.doctorsWithMostSickLeaves());
    }

    @GetMapping("/patient-history/{patientId}")
    public ResponseEntity<?> patientHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(reportService.patientHistory(patientId));
    }
}
