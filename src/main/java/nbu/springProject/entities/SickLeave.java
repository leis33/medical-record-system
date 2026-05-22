package nbu.springProject.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Integer numberOfDays;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", unique = true)
    private Examination examination;
}
