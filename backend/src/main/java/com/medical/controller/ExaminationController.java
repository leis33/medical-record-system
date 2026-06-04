package com.medical.controller;

import com.medical.dto.ExaminationDto;
import com.medical.service.ExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ExaminationController {

    private final ExaminationService examinationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> findAll() {
        return ResponseEntity.ok(examinationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExaminationDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ExaminationDto>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(examinationService.findByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> findByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(examinationService.findByDoctorId(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/period")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> findByDoctorAndPeriod(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(examinationService.findByDoctorAndPeriod(doctorId, from, to));
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<ExaminationDto>> findByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(examinationService.findByPeriod(from, to));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ExaminationDto> create(@Valid @RequestBody ExaminationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examinationService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<ExaminationDto> update(@PathVariable Long id, @Valid @RequestBody ExaminationDto dto) {
        return ResponseEntity.ok(examinationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examinationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
