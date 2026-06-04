package com.medical.repository;

import com.medical.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT d, COUNT(e) as cnt FROM Diagnosis d JOIN d.examinations e GROUP BY d ORDER BY cnt DESC")
    List<Object[]> findMostCommonDiagnoses();
}
