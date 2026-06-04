package com.medical.repository;

import com.medical.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    List<SickLeave> findByExaminationPatientId(Long patientId);

    @Query("SELECT MONTH(sl.startDate), YEAR(sl.startDate), COUNT(sl) FROM SickLeave sl GROUP BY YEAR(sl.startDate), MONTH(sl.startDate) ORDER BY COUNT(sl) DESC")
    List<Object[]> findMonthWithMostSickLeaves();

    @Query("SELECT e.doctor, COUNT(sl) as cnt FROM SickLeave sl JOIN sl.examination e GROUP BY e.doctor ORDER BY cnt DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();
}
