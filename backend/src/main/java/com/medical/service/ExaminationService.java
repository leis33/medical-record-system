package com.medical.service;

import com.medical.dto.ExaminationDto;
import com.medical.dto.SickLeaveDto;
import com.medical.entity.*;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final UserRepository userRepository;

    public List<ExaminationDto> findAll() {
        return examinationRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ExaminationDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<ExaminationDto> findByPatientId(Long patientId) {
        return examinationRepository.findByPatientId(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ExaminationDto> findByDoctorId(Long doctorId) {
        return examinationRepository.findByDoctorId(doctorId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ExaminationDto> findByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to) {
        return examinationRepository.findByDoctorAndPeriod(doctorId, from, to).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ExaminationDto> findByPeriod(LocalDate from, LocalDate to) {
        return examinationRepository.findByPeriod(from, to).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ExaminationDto create(ExaminationDto dto) {
        Doctor doctor = doctorService.getEntityById(dto.getDoctorId());
        Patient patient = patientService.getEntityById(dto.getPatientId());
        Diagnosis diagnosis = dto.getDiagnosisId() != null ? diagnosisService.getEntityById(dto.getDiagnosisId()) : null;

        Examination exam = Examination.builder()
                .date(dto.getDate())
                .doctor(doctor)
                .patient(patient)
                .diagnosis(diagnosis)
                .prescribedTreatment(dto.getPrescribedTreatment())
                .cost(dto.getCost())
                .paidByNHIF(patient.isHasHealthInsurance())
                .build();
        return toDto(examinationRepository.save(exam));
    }

    @Transactional
    public ExaminationDto update(Long id, ExaminationDto dto) {
        Examination exam = getOrThrow(id);
        checkEditPermission(exam);
        Patient patient = patientService.getEntityById(dto.getPatientId());
        Diagnosis diagnosis = dto.getDiagnosisId() != null ? diagnosisService.getEntityById(dto.getDiagnosisId()) : null;

        exam.setDate(dto.getDate());
        exam.setPatient(patient);
        exam.setDiagnosis(diagnosis);
        exam.setPrescribedTreatment(dto.getPrescribedTreatment());
        exam.setCost(dto.getCost());
        exam.setPaidByNHIF(patient.isHasHealthInsurance());
        return toDto(examinationRepository.save(exam));
    }

    @Transactional
    public void delete(Long id) {
        Examination exam = getOrThrow(id);
        checkEditPermission(exam);
        examinationRepository.deleteById(id);
    }

    private void checkEditPermission(Examination exam) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        if (!exam.getDoctor().getId().equals(user.getProfileId()))
            throw new AccessDeniedException("You can only edit your own examinations");
    }

    private Examination getOrThrow(Long id) {
        return examinationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Examination", id));
    }

    public ExaminationDto toDto(Examination e) {
        ExaminationDto dto = new ExaminationDto();
        dto.setId(e.getId());
        dto.setDate(e.getDate());
        dto.setDoctorId(e.getDoctor().getId());
        dto.setDoctorName(e.getDoctor().getName());
        dto.setPatientId(e.getPatient().getId());
        dto.setPatientName(e.getPatient().getName());
        dto.setCost(e.getCost());
        dto.setPaidByNHIF(e.isPaidByNHIF());
        dto.setPrescribedTreatment(e.getPrescribedTreatment());
        if (e.getDiagnosis() != null) {
            dto.setDiagnosisId(e.getDiagnosis().getId());
            dto.setDiagnosisDescription(e.getDiagnosis().getDescription());
        }
        if (e.getSickLeaves() != null) {
            dto.setSickLeaves(e.getSickLeaves().stream().map(sl -> {
                SickLeaveDto s = new SickLeaveDto();
                s.setId(sl.getId());
                s.setStartDate(sl.getStartDate());
                s.setNumberOfDays(sl.getNumberOfDays());
                s.setExaminationId(e.getId());
                return s;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
