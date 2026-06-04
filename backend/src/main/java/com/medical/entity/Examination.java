package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "examinations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @NotBlank
    private String prescribedTreatment;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cost;

    // Computed: if patient has health insurance, NHIF pays; otherwise patient pays
    private boolean paidByNHIF;

    @OneToMany(mappedBy = "examination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SickLeave> sickLeaves;
}
