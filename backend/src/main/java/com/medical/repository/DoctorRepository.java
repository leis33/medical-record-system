package com.medical.repository;

import com.medical.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByIdentificationNumber(String identificationNumber);
    boolean existsByIdentificationNumber(String identificationNumber);
    List<Doctor> findByCanBePersonalDoctorTrue();

    @Query("SELECT d FROM Doctor d LEFT JOIN d.examinations e GROUP BY d ORDER BY COUNT(e) DESC")
    List<Object[]> findDoctorsOrderedByExaminationCount();

    @Query("SELECT d, COUNT(sl) as slCount FROM Doctor d JOIN d.examinations e JOIN e.sickLeaves sl GROUP BY d ORDER BY slCount DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();

    @Query("SELECT d, COUNT(DISTINCT e.patient) as patientCount FROM Doctor d JOIN d.examinations e GROUP BY d ORDER BY patientCount DESC")
    List<Object[]> findDoctorsWithVisitCounts();
}
