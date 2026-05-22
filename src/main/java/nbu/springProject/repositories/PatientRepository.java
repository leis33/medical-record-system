package nbu.springProject.repositories;

import com.emr.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPersonalIdentificationNumber(String pin);
    List<Patient> findByPersonalDoctorId(Long doctorId);
    Optional<Patient> findByUserId(Long userId);
    boolean existsByPersonalIdentificationNumber(String pin);

    @Query("SELECT p FROM Patient p JOIN p.examinations e WHERE e.diagnosis.id = :diagnosisId")
    List<Patient> findPatientsByDiagnosis(@Param("diagnosisId") Long diagnosisId);

    @Query("SELECT p FROM Patient p WHERE p.personalDoctor.id = :doctorId")
    List<Patient> findPatientsByPersonalDoctor(@Param("doctorId") Long doctorId);
}
