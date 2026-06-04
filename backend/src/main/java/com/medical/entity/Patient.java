package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "\\d{10}", message = "Personal identification number must be exactly 10 digits")
    private String personalIdentificationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;

    private boolean hasHealthInsurance;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Examination> examinations;
}
