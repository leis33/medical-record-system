package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "doctors")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String identificationNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String specialty;

    private boolean canBePersonalDoctor;

    @OneToMany(mappedBy = "personalDoctor", fetch = FetchType.LAZY)
    private List<Patient> patients;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Examination> examinations;
}
