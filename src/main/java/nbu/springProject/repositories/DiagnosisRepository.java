package nbu.springProject.repositories;

import com.emr.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByCode(String code);
    List<Diagnosis> findByNameContainingIgnoreCase(String name);
    boolean existsByCode(String code);

    @Query("SELECT d, COUNT(e) as count FROM Diagnosis d LEFT JOIN d.examinations e GROUP BY d ORDER BY count DESC")
    List<Object[]> findMostCommonDiagnoses();
}
