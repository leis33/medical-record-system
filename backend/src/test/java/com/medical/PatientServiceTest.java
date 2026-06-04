package com.medical;

import com.medical.dto.PatientDto;
import com.medical.entity.Doctor;
import com.medical.entity.Patient;
import com.medical.exception.DuplicateResourceException;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.PatientRepository;
import com.medical.service.DoctorService;
import com.medical.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock PatientRepository patientRepository;
    @Mock DoctorService doctorService;
    @InjectMocks PatientService patientService;

    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(1L).name("Dr. Test").specialty("GP").canBePersonalDoctor(true).build();
        patient = Patient.builder().id(1L).name("Test Patient")
                .personalIdentificationNumber("1234567890")
                .personalDoctor(doctor).hasHealthInsurance(true).build();
    }

    @Test
    void findAll_returnsList() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        List<PatientDto> result = patientService.findAll();
        assertEquals(1, result.size());
        assertEquals("Test Patient", result.get(0).getName());
    }

    @Test
    void findById_found() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        PatientDto result = patientService.findById(1L);
        assertEquals("1234567890", result.getPersonalIdentificationNumber());
    }

    @Test
    void findById_notFound_throws() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> patientService.findById(99L));
    }

    @Test
    void create_duplicatePIN_throws() {
        PatientDto dto = new PatientDto();
        dto.setName("New Patient");
        dto.setPersonalIdentificationNumber("1234567890");
        when(patientRepository.existsByPersonalIdentificationNumber("1234567890")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> patientService.create(dto));
    }

    @Test
    void create_validPatient_succeeds() {
        PatientDto dto = new PatientDto();
        dto.setName("New Patient");
        dto.setPersonalIdentificationNumber("9876543210");
        dto.setPersonalDoctorId(1L);
        when(patientRepository.existsByPersonalIdentificationNumber("9876543210")).thenReturn(false);
        when(doctorService.getEntityById(1L)).thenReturn(doctor);
        when(patientRepository.save(any())).thenReturn(patient);
        PatientDto result = patientService.create(dto);
        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }
}
