package nbu.springProject.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "examinations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate examinationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescribedTreatment;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    // true = paid by NHIF, false = paid by patient
    @Column(nullable = false)
    private boolean paidByNhif = false;

    @OneToOne(mappedBy = "examination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SickLeave sickLeave;
}