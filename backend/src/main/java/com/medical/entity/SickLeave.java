package com.medical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate startDate;

    @Min(1)
    private int numberOfDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id")
    private Examination examination;
}
