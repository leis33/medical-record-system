package com.medical.repository;

import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPersonalIdentificationNumber(String pin);
    boolean existsByPersonalIdentificationNumber(String pin);
    List<Patient> findByPersonalDoctor(Doctor doctor);
    List<Patient> findByPersonalDoctorId(Long doctorId);

    @Query("SELECT p FROM Patient p JOIN p.examinations e WHERE e.diagnosis.id = :diagnosisId")
    List<Patient> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    @Query("SELECT p.personalDoctor, COUNT(p) as cnt FROM Patient p GROUP BY p.personalDoctor ORDER BY cnt DESC")
    List<Object[]> countPatientsByGP();
}
