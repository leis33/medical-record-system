package nbu.springProject.repositories;

import com.emr.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {
    Optional<SickLeave> findByExaminationId(Long examinationId);

    @Query("SELECT MONTH(sl.startDate), YEAR(sl.startDate), COUNT(sl) " +
            "FROM SickLeave sl GROUP BY YEAR(sl.startDate), MONTH(sl.startDate) " +
            "ORDER BY COUNT(sl) DESC")
    List<Object[]> findMonthWithMostSickLeaves();

    @Query("SELECT sl FROM SickLeave sl JOIN sl.examination e WHERE e.patient.id = :patientId")
    List<SickLeave> findByPatientId(Long patientId);
}
