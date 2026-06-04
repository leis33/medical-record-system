package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "diagnoses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String code;

    @NotBlank
    private String description;

    @OneToMany(mappedBy = "diagnosis", fetch = FetchType.LAZY)
    private List<Examination> examinations;
}
