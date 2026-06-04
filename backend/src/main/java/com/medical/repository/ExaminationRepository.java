package com.medical.repository;

import com.medical.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatientId(Long patientId);
    List<Examination> findByDoctorId(Long doctorId);

    @Query("SELECT e FROM Examination e WHERE e.doctor.id = :doctorId AND e.date BETWEEN :from AND :to")
    List<Examination> findByDoctorAndPeriod(@Param("doctorId") Long doctorId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);

    @Query("SELECT e FROM Examination e WHERE e.date BETWEEN :from AND :to")
    List<Examination> findByPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("SELECT SUM(e.cost) FROM Examination e WHERE e.paidByNHIF = false")
    BigDecimal totalPaidByPatients();

    @Query("SELECT e.doctor, SUM(e.cost) FROM Examination e WHERE e.paidByNHIF = false GROUP BY e.doctor")
    List<Object[]> totalPaidByPatientsByDoctor();

    @Query("SELECT COUNT(DISTINCT e.patient) FROM Examination e WHERE e.doctor.id = :doctorId")
    Long countDistinctPatientsByDoctor(@Param("doctorId") Long doctorId);
}
