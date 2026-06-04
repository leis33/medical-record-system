package com.medical;

import com.medical.dto.DoctorDto;
import com.medical.entity.Doctor;
import com.medical.exception.DuplicateResourceException;
import com.medical.exception.ResourceNotFoundException;
import com.medical.repository.DoctorRepository;
import com.medical.service.DoctorService;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private DoctorDto doctorDto;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder().id(1L).identificationNumber("DOC001")
                .name("Dr. Test").specialty("General").canBePersonalDoctor(true).build();
        doctorDto = new DoctorDto();
        doctorDto.setIdentificationNumber("DOC001");
        doctorDto.setName("Dr. Test");
        doctorDto.setSpecialty("General");
        doctorDto.setCanBePersonalDoctor(true);
    }

    @Test
    void findAll_returnsAll() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        List<DoctorDto> result = doctorService.findAll();
        assertEquals(1, result.size());
        assertEquals("Dr. Test", result.get(0).getName());
    }

    @Test
    void findById_exists_returnsDto() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        DoctorDto result = doctorService.findById(1L);
        assertEquals("DOC001", result.getIdentificationNumber());
    }

    @Test
    void findById_notFound_throws() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> doctorService.findById(99L));
    }

    @Test
    void create_newDoctor_succeeds() {
        when(doctorRepository.existsByIdentificationNumber("DOC001")).thenReturn(false);
        when(doctorRepository.save(any())).thenReturn(doctor);
        DoctorDto result = doctorService.create(doctorDto);
        assertEquals("Dr. Test", result.getName());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void create_duplicateId_throws() {
        when(doctorRepository.existsByIdentificationNumber("DOC001")).thenReturn(true);
        assertThrows(DuplicateResourceException.class, () -> doctorService.create(doctorDto));
    }

    @Test
    void delete_exists_succeeds() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        doctorService.delete(1L);
        verify(doctorRepository).deleteById(1L);
    }
}
