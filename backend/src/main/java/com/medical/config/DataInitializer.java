package com.medical.config;

import com.medical.entity.*;
import com.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) return;

        // Create admin
        userRepository.save(User.builder()
                .username("admin").password(passwordEncoder.encode("admin123"))
                .email("admin@medical.com").role(Role.ADMIN).build());

        // Create doctors
        Doctor d1 = doctorRepository.save(Doctor.builder()
                .identificationNumber("DOC001").name("Dr. Ivan Petrov")
                .specialty("General Practice").canBePersonalDoctor(true).build());
        Doctor d2 = doctorRepository.save(Doctor.builder()
                .identificationNumber("DOC002").name("Dr. Maria Ivanova")
                .specialty("Cardiology").canBePersonalDoctor(false).build());

        userRepository.save(User.builder()
                .username("doctor1").password(passwordEncoder.encode("doctor123"))
                .email("doctor1@medical.com").role(Role.DOCTOR).profileId(d1.getId()).build());
        userRepository.save(User.builder()
                .username("doctor2").password(passwordEncoder.encode("doctor123"))
                .email("doctor2@medical.com").role(Role.DOCTOR).profileId(d2.getId()).build());

        // Create patients
        Patient p1 = patientRepository.save(Patient.builder()
                .name("Georgi Georgiev").personalIdentificationNumber("8501015432")
                .personalDoctor(d1).hasHealthInsurance(true).build());
        Patient p2 = patientRepository.save(Patient.builder()
                .name("Anna Dimitrova").personalIdentificationNumber("9203127654")
                .personalDoctor(d1).hasHealthInsurance(false).build());

        userRepository.save(User.builder()
                .username("patient1").password(passwordEncoder.encode("patient123"))
                .email("patient1@medical.com").role(Role.PATIENT).profileId(p1.getId()).build());
        userRepository.save(User.builder()
                .username("patient2").password(passwordEncoder.encode("patient123"))
                .email("patient2@medical.com").role(Role.PATIENT).profileId(p2.getId()).build());

        // Create diagnoses
        Diagnosis diag1 = diagnosisRepository.save(Diagnosis.builder().code("J06").description("Acute upper respiratory infection").build());
        Diagnosis diag2 = diagnosisRepository.save(Diagnosis.builder().code("I10").description("Essential hypertension").build());
        Diagnosis diag3 = diagnosisRepository.save(Diagnosis.builder().code("K29").description("Gastritis").build());

        // Create examinations
        Examination e1 = examinationRepository.save(Examination.builder()
                .date(LocalDate.now().minusDays(10)).doctor(d1).patient(p1)
                .diagnosis(diag1).prescribedTreatment("Rest, fluids, paracetamol")
                .cost(new BigDecimal("30.00")).paidByNHIF(true).build());

        Examination e2 = examinationRepository.save(Examination.builder()
                .date(LocalDate.now().minusDays(5)).doctor(d2).patient(p2)
                .diagnosis(diag2).prescribedTreatment("Amlodipine 5mg daily")
                .cost(new BigDecimal("50.00")).paidByNHIF(false).build());

        // Sick leave for e1
        sickLeaveRepository.save(SickLeave.builder()
                .startDate(LocalDate.now().minusDays(10))
                .numberOfDays(3).examination(e1).build());
    }
}
