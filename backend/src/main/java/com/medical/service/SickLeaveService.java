package com.medical.service;

import com.medical.dto.SickLeaveDto;
import com.medical.entity.Examination;
import com.medical.entity.SickLeave;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExaminationRepository examinationRepository;

    public List<SickLeaveDto> findAll() {
        return sickLeaveRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public SickLeaveDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public List<SickLeaveDto> findByPatientId(Long patientId) {
        return sickLeaveRepository.findByExaminationPatientId(patientId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public SickLeaveDto create(SickLeaveDto dto) {
        Examination examination = examinationRepository.findById(dto.getExaminationId())
                .orElseThrow(() -> new ResourceNotFoundException("Examination", dto.getExaminationId()));
        SickLeave sl = SickLeave.builder()
                .startDate(dto.getStartDate())
                .numberOfDays(dto.getNumberOfDays())
                .examination(examination)
                .build();
        return toDto(sickLeaveRepository.save(sl));
    }

    @Transactional
    public SickLeaveDto update(Long id, SickLeaveDto dto) {
        SickLeave sl = getOrThrow(id);
        sl.setStartDate(dto.getStartDate());
        sl.setNumberOfDays(dto.getNumberOfDays());
        return toDto(sickLeaveRepository.save(sl));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        sickLeaveRepository.deleteById(id);
    }

    private SickLeave getOrThrow(Long id) {
        return sickLeaveRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SickLeave", id));
    }

    public SickLeaveDto toDto(SickLeave sl) {
        SickLeaveDto dto = new SickLeaveDto();
        dto.setId(sl.getId());
        dto.setStartDate(sl.getStartDate());
        dto.setNumberOfDays(sl.getNumberOfDays());
        dto.setExaminationId(sl.getExamination().getId());
        return dto;
    }
}
