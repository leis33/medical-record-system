package nbu.springProject.repositories;

import com.emr.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findByPatientId(Long patientId);
    List<Examination> findByDoctorId(Long doctorId);
    List<Examination> findByDoctorIdAndExaminationDateBetween(Long doctorId, LocalDate start, LocalDate end);
    List<Examination> findByExaminationDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(e.cost) FROM Examination e WHERE e.paidByNhif = false")
    BigDecimal getTotalCostPaidByPatients();

    @Query("SELECT e.doctor, SUM(e.cost) FROM Examination e WHERE e.paidByNhif = false GROUP BY e.doctor")
    List<Object[]> getTotalCostPaidByPatientsPerDoctor();

    @Query("SELECT e FROM Examination e WHERE e.patient.id = :patientId ORDER BY e.examinationDate DESC")
    List<Examination> findPatientHistory(@Param("patientId") Long patientId);

    @Query("SELECT e FROM Examination e WHERE e.doctor.id = :doctorId " +
            "AND (:start IS NULL OR e.examinationDate >= :start) " +
            "AND (:end IS NULL OR e.examinationDate <= :end)")
    List<Examination> findByDoctorAndPeriod(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}