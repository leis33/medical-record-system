package com.medical.service;

import com.medical.dto.DiagnosisDto;
import com.medical.dto.DoctorDto;
import com.medical.dto.ExaminationDto;
import com.medical.dto.PatientDto;
import com.medical.entity.Diagnosis;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import com.medical.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final ExaminationService examinationService;

    // 1. Patients with given diagnosis
    public List<PatientDto> patientsByDiagnosis(Long diagnosisId) {
        return patientRepository.findByDiagnosisId(diagnosisId).stream()
                .map(patientService::toDto).collect(Collectors.toList());
    }

    // 2. Most common diagnosis
    public List<Map<String, Object>> mostCommonDiagnoses() {
        return diagnosisRepository.findMostCommonDiagnoses().stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("diagnosis", diagnosisService.toDto((Diagnosis) row[0]));
            m.put("count", row[1]);
            return m;
        }).collect(Collectors.toList());
    }

    // 3. Patients to a given GP
    public List<PatientDto> patientsByGP(Long doctorId) {
        return patientRepository.findByPersonalDoctorId(doctorId).stream()
                .map(patientService::toDto).collect(Collectors.toList());
    }

    // 4. Total examination value paid by patients
    public BigDecimal totalPaidByPatients() {
        BigDecimal total = examinationRepository.totalPaidByPatients();
        return total != null ? total : BigDecimal.ZERO;
    }

    // 5. Value paid by patients per doctor
    public List<Map<String, Object>> paidByPatientsByDoctor() {
        return examinationRepository.totalPaidByPatientsByDoctor().stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("doctor", doctorService.toDto((Doctor) row[0]));
            m.put("total", row[1]);
            return m;
        }).collect(Collectors.toList());
    }

    // 6. Number of patients with each GP
    public List<Map<String, Object>> patientCountByGP() {
        return patientRepository.countPatientsByGP().stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("doctor", doctorService.toDto((Doctor) row[0]));
            m.put("patientCount", row[1]);
            return m;
        }).collect(Collectors.toList());
    }

    // 7. Number of visits to each doctor
    public List<Map<String, Object>> visitCountByDoctor() {
        return examinationRepository.totalPaidByPatientsByDoctor().stream().map(row -> {
            Doctor doc = (Doctor) row[0];
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("doctor", doctorService.toDto(doc));
            m.put("visitCount", examinationRepository.findByDoctorId(doc.getId()).size());
            return m;
        }).collect(Collectors.toList());
    }

    // 8. Month with most sick leaves
    public List<Map<String, Object>> monthWithMostSickLeaves() {
        return sickLeaveRepository.findMonthWithMostSickLeaves().stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("month", row[0]);
            m.put("year", row[1]);
            m.put("count", row[2]);
            return m;
        }).collect(Collectors.toList());
    }

    // 9. Doctors with most sick leaves
    public List<Map<String, Object>> doctorsWithMostSickLeaves() {
        return sickLeaveRepository.findDoctorsWithMostSickLeaves().stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("doctor", doctorService.toDto((Doctor) row[0]));
            m.put("sickLeaveCount", row[1]);
            return m;
        }).collect(Collectors.toList());
    }

    // 10. Patient visit history
    public List<ExaminationDto> patientHistory(Long patientId) {
        return examinationRepository.findByPatientId(patientId).stream()
                .map(examinationService::toDto).collect(Collectors.toList());
    }
}
