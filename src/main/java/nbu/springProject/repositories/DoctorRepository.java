package nbu.springProject.repositories;

import com.emr.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByIdentificationNumber(String identificationNumber);
    List<Doctor> findByCanBePersonalDoctorTrue();
    Optional<Doctor> findByUserId(Long userId);
    boolean existsByIdentificationNumber(String identificationNumber);

    @Query("SELECT d, COUNT(p) as patientCount FROM Doctor d LEFT JOIN d.patients p GROUP BY d ORDER BY patientCount DESC")
    List<Object[]> findDoctorsWithPatientCount();

    @Query("SELECT d, COUNT(e) as visitCount FROM Doctor d LEFT JOIN d.examinations e GROUP BY d ORDER BY visitCount DESC")
    List<Object[]> findDoctorsWithVisitCount();

    @Query("SELECT d, COUNT(sl) as sickLeaveCount FROM Doctor d " +
            "JOIN d.examinations e JOIN e.sickLeave sl GROUP BY d ORDER BY sickLeaveCount DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();
}
