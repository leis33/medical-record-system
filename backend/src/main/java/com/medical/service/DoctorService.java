package com.medical.service;

import com.medical.dto.DoctorDto;
import com.medical.entity.Doctor;
import com.medical.exception.DuplicateResourceException;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorDto> findAll() {
        return doctorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DoctorDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<DoctorDto> findPersonalDoctors() {
        return doctorRepository.findByCanBePersonalDoctorTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public DoctorDto create(DoctorDto dto) {
        if (doctorRepository.existsByIdentificationNumber(dto.getIdentificationNumber()))
            throw new DuplicateResourceException("Doctor with ID number already exists: " + dto.getIdentificationNumber());
        Doctor doctor = Doctor.builder()
                .identificationNumber(dto.getIdentificationNumber())
                .name(dto.getName())
                .specialty(dto.getSpecialty())
                .canBePersonalDoctor(dto.isCanBePersonalDoctor())
                .build();
        return toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorDto update(Long id, DoctorDto dto) {
        Doctor doctor = getOrThrow(id);
        if (!doctor.getIdentificationNumber().equals(dto.getIdentificationNumber())
                && doctorRepository.existsByIdentificationNumber(dto.getIdentificationNumber()))
            throw new DuplicateResourceException("Identification number already in use");
        doctor.setName(dto.getName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setCanBePersonalDoctor(dto.isCanBePersonalDoctor());
        doctor.setIdentificationNumber(dto.getIdentificationNumber());
        return toDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        doctorRepository.deleteById(id);
    }

    private Doctor getOrThrow(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    public DoctorDto toDto(Doctor d) {
        DoctorDto dto = new DoctorDto();
        dto.setId(d.getId());
        dto.setIdentificationNumber(d.getIdentificationNumber());
        dto.setName(d.getName());
        dto.setSpecialty(d.getSpecialty());
        dto.setCanBePersonalDoctor(d.isCanBePersonalDoctor());
        dto.setPatientCount(d.getPatients() != null ? d.getPatients().size() : 0);
        dto.setExaminationCount(d.getExaminations() != null ? d.getExaminations().size() : 0);
        return dto;
    }

    public Doctor getEntityById(Long id) {
        return getOrThrow(id);
    }
}
