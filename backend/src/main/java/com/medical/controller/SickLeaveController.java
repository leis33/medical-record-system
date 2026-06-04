package com.medical.controller;

import com.medical.dto.SickLeaveDto;
import com.medical.service.SickLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<List<SickLeaveDto>> findAll() {
        return ResponseEntity.ok(sickLeaveService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SickLeaveDto>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(sickLeaveService.findByPatientId(patientId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<SickLeaveDto> create(@Valid @RequestBody SickLeaveDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sickLeaveService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<SickLeaveDto> update(@PathVariable Long id, @Valid @RequestBody SickLeaveDto dto) {
        return ResponseEntity.ok(sickLeaveService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sickLeaveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
