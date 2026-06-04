package com.medical.service;

import com.medical.dto.PatientDto;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import com.medical.exception.DuplicateResourceException;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorService doctorService;

    public List<PatientDto> findAll() {
        return patientRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public PatientDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<PatientDto> findByDoctorId(Long doctorId) {
        return patientRepository.findByPersonalDoctorId(doctorId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public PatientDto create(PatientDto dto) {
        if (patientRepository.existsByPersonalIdentificationNumber(dto.getPersonalIdentificationNumber()))
            throw new DuplicateResourceException("Patient with PIN already exists: " + dto.getPersonalIdentificationNumber());
        Doctor doctor = dto.getPersonalDoctorId() != null ? doctorService.getEntityById(dto.getPersonalDoctorId()) : null;
        Patient patient = Patient.builder()
                .name(dto.getName())
                .personalIdentificationNumber(dto.getPersonalIdentificationNumber())
                .personalDoctor(doctor)
                .hasHealthInsurance(dto.isHasHealthInsurance())
                .build();
        return toDto(patientRepository.save(patient));
    }

    @Transactional
    public PatientDto update(Long id, PatientDto dto) {
        Patient patient = getOrThrow(id);
        if (!patient.getPersonalIdentificationNumber().equals(dto.getPersonalIdentificationNumber())
                && patientRepository.existsByPersonalIdentificationNumber(dto.getPersonalIdentificationNumber()))
            throw new DuplicateResourceException("PIN already in use");
        Doctor doctor = dto.getPersonalDoctorId() != null ? doctorService.getEntityById(dto.getPersonalDoctorId()) : null;
        patient.setName(dto.getName());
        patient.setPersonalIdentificationNumber(dto.getPersonalIdentificationNumber());
        patient.setPersonalDoctor(doctor);
        patient.setHasHealthInsurance(dto.isHasHealthInsurance());
        return toDto(patientRepository.save(patient));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        patientRepository.deleteById(id);
    }

    public Patient getEntityById(Long id) {
        return getOrThrow(id);
    }

    private Patient getOrThrow(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    public PatientDto toDto(Patient p) {
        PatientDto dto = new PatientDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPersonalIdentificationNumber(p.getPersonalIdentificationNumber());
        dto.setHasHealthInsurance(p.isHasHealthInsurance());
        dto.setExaminationCount(p.getExaminations() != null ? p.getExaminations().size() : 0);
        if (p.getPersonalDoctor() != null) {
            dto.setPersonalDoctorId(p.getPersonalDoctor().getId());
            dto.setPersonalDoctorName(p.getPersonalDoctor().getName());
        }
        return dto;
    }
}
