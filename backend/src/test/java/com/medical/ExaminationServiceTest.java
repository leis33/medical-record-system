package com.medical;

import com.medical.dto.ExaminationDto;
import com.medical.entity.*;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.ExaminationRepository;
import com.medical.repository.UserRepository;
import com.medical.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

    @Mock ExaminationRepository examinationRepository;
    @Mock DoctorService doctorService;
    @Mock PatientService patientService;
    @Mock DiagnosisService diagnosisService;
    @Mock UserRepository userRepository;
    @InjectMocks ExaminationService examinationService;

    private Doctor doctor;
    private Patient patient;
    private Examination examination;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(1L).name("Dr. Smith").specialty("GP").build();
        patient = Patient.builder().id(1L).name("John Doe")
                .personalIdentificationNumber("1234567890").hasHealthInsurance(true).build();
        examination = Examination.builder().id(1L).date(LocalDate.now())
                .doctor(doctor).patient(patient).prescribedTreatment("Rest")
                .cost(new BigDecimal("30.00")).paidByNHIF(true).build();
    }

    @Test
    void findByPatientId_returnsList() {
        when(examinationRepository.findByPatientId(1L)).thenReturn(List.of(examination));
        List<ExaminationDto> result = examinationService.findByPatientId(1L);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDoctorId());
    }

    @Test
    void create_insuredPatient_paidByNHIF() {
        ExaminationDto dto = new ExaminationDto();
        dto.setDate(LocalDate.now());
        dto.setDoctorId(1L);
        dto.setPatientId(1L);
        dto.setPrescribedTreatment("Test treatment");
        dto.setCost(new BigDecimal("50.00"));

        when(doctorService.getEntityById(1L)).thenReturn(doctor);
        when(patientService.getEntityById(1L)).thenReturn(patient);
        when(examinationRepository.save(any())).thenReturn(examination);

        ExaminationDto result = examinationService.create(dto);
        assertTrue(result.isPaidByNHIF());
    }

    @Test
    void findById_notFound_throws() {
        when(examinationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> examinationService.findById(99L));
    }
}
