package com.medical.controller;

import com.medical.dto.DiagnosisDto;
import com.medical.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public ResponseEntity<List<DiagnosisDto>> findAll() {
        return ResponseEntity.ok(diagnosisService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<DiagnosisDto> create(@Valid @RequestBody DiagnosisDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<DiagnosisDto> update(@PathVariable Long id, @Valid @RequestBody DiagnosisDto dto) {
        return ResponseEntity.ok(diagnosisService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diagnosisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
