package com.medical.service;

import com.medical.dto.DiagnosisDto;
import com.medical.entity.Diagnosis;
import com.medical.exception.DuplicateResourceException;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    public List<DiagnosisDto> findAll() {
        return diagnosisRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public DiagnosisDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public DiagnosisDto create(DiagnosisDto dto) {
        if (diagnosisRepository.existsByCode(dto.getCode()))
            throw new DuplicateResourceException("Diagnosis code already exists: " + dto.getCode());
        Diagnosis d = Diagnosis.builder().code(dto.getCode()).description(dto.getDescription()).build();
        return toDto(diagnosisRepository.save(d));
    }

    @Transactional
    public DiagnosisDto update(Long id, DiagnosisDto dto) {
        Diagnosis d = getOrThrow(id);
        if (!d.getCode().equals(dto.getCode()) && diagnosisRepository.existsByCode(dto.getCode()))
            throw new DuplicateResourceException("Diagnosis code already in use");
        d.setCode(dto.getCode());
        d.setDescription(dto.getDescription());
        return toDto(diagnosisRepository.save(d));
    }

    @Transactional
    public void delete(Long id) {
        getOrThrow(id);
        diagnosisRepository.deleteById(id);
    }

    public Diagnosis getEntityById(Long id) {
        return getOrThrow(id);
    }

    private Diagnosis getOrThrow(Long id) {
        return diagnosisRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));
    }

    public DiagnosisDto toDto(Diagnosis d) {
        DiagnosisDto dto = new DiagnosisDto();
        dto.setId(d.getId());
        dto.setCode(d.getCode());
        dto.setDescription(d.getDescription());
        dto.setExaminationCount(d.getExaminations() != null ? d.getExaminations().size() : 0);
        return dto;
    }
}
