# Medical Record System

A full-stack Electronic Medical Records web application built with **React** (frontend) and **Spring Boot** (backend), using MySQL and Gradle.

---

## 📁 Project Structure

```
medical-record-system/
├── backend/                          # Spring Boot (Gradle)
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/
│       ├── main/
│       │   ├── java/com/medical/
│       │   │   ├── MedicalRecordApplication.java
│       │   │   ├── config/
│       │   │   │   ├── SecurityConfig.java       # JWT security, CORS, roles
│       │   │   │   └── DataInitializer.java      # Seeds demo data on startup
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── DoctorController.java
│       │   │   │   ├── PatientController.java
│       │   │   │   ├── DiagnosisController.java
│       │   │   │   ├── ExaminationController.java
│       │   │   │   ├── SickLeaveController.java
│       │   │   │   └── ReportController.java
│       │   │   ├── dto/
│       │   │   │   ├── AuthDto.java
│       │   │   │   ├── DoctorDto.java
│       │   │   │   ├── PatientDto.java
│       │   │   │   ├── DiagnosisDto.java
│       │   │   │   ├── ExaminationDto.java
│       │   │   │   └── SickLeaveDto.java
│       │   │   ├── entity/
│       │   │   │   ├── User.java
│       │   │   │   ├── Role.java (enum: ADMIN, DOCTOR, PATIENT)
│       │   │   │   ├── Doctor.java
│       │   │   │   ├── Patient.java
│       │   │   │   ├── Diagnosis.java
│       │   │   │   ├── Examination.java
│       │   │   │   └── SickLeave.java
│       │   │   ├── exception/
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── DuplicateResourceException.java
│       │   │   ├── repository/
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── DoctorRepository.java
│       │   │   │   ├── PatientRepository.java
│       │   │   │   ├── DiagnosisRepository.java
│       │   │   │   ├── ExaminationRepository.java
│       │   │   │   └── SickLeaveRepository.java
│       │   │   ├── security/
│       │   │   │   ├── JwtUtils.java
│       │   │   │   ├── JwtAuthFilter.java
│       │   │   │   └── UserDetailsServiceImpl.java
│       │   │   └── service/
│       │   │       ├── AuthService.java
│       │   │       ├── DoctorService.java
│       │   │       ├── PatientService.java
│       │   │       ├── DiagnosisService.java
│       │   │       ├── ExaminationService.java
│       │   │       ├── SickLeaveService.java
│       │   │       └── ReportService.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
│           ├── java/com/medical/
│           │   ├── DoctorServiceTest.java
│           │   ├── PatientServiceTest.java
│           │   ├── ExaminationServiceTest.java
│           │   └── AuthControllerTest.java
│           └── resources/
│               └── application-test.properties   # H2 in-memory for tests
│
└── frontend/                         # React (CRA)
    ├── package.json
    ├── public/
    │   └── index.html
    └── src/
        ├── App.js                    # Routes
        ├── index.js
        ├── index.css                 # Global styles (IBM Plex Sans)
        ├── context/
        │   └── AuthContext.js        # JWT auth state
        ├── services/
        │   └── api.js                # Axios + all API calls
        └── pages/
            ├── LoginPage.js
            ├── DashboardPage.js
            ├── DoctorsPage.js
            ├── PatientsPage.js
            ├── DiagnosesPage.js
            ├── ExaminationsPage.js
            ├── SickLeavesPage.js
            └── ReportsPage.js
```

---

## 🚀 Setup & Run

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8+

### 1. Database
```sql
CREATE DATABASE medical_db;
```
The schema is auto-created by Hibernate (`ddl-auto=update`).

### 2. Backend
```bash
cd backend
# Edit src/main/resources/application.properties if needed (DB credentials)
./gradlew bootRun
# Runs on http://localhost:8080
```

### 3. Frontend
```bash
cd frontend
npm install
npm start
# Runs on http://localhost:3000
```

---

## 🔐 Default Demo Accounts

| Username  | Password    | Role    |
|-----------|-------------|---------|
| admin     | admin123    | ADMIN   |
| doctor1   | doctor123   | DOCTOR  |
| doctor2   | doctor123   | DOCTOR  |
| patient1  | patient123  | PATIENT |
| patient2  | patient123  | PATIENT |

---

## 🔑 Access Control

| Feature             | ADMIN | DOCTOR | PATIENT |
|---------------------|-------|--------|---------|
| Manage doctors      | ✅    | 👁      | 👁       |
| Manage patients     | ✅    | 👁      | 👁 (own) |
| Manage diagnoses    | ✅    | ✅      | ❌       |
| Create examinations | ✅    | ✅      | ❌       |
| Edit own exams only | N/A   | ✅      | N/A      |
| Issue sick leaves   | ✅    | ✅      | ❌       |
| View all reports    | ✅    | ✅      | ❌       |
| View own records    | ✅    | ✅      | ✅       |

---

## 📊 Available Reports

1. Patients with a given diagnosis
2. Most common diagnosis
3. Patients registered with a given GP
4. Total examination value paid by patients (no insurance)
5. Payment totals per doctor
6. Patient count per GP
7. Visit count per doctor
8. Month with most sick leaves issued
9. Doctors who issued most sick leaves
10. Full visit history of a patient

---

## 🧪 Running Tests
```bash
cd backend
./gradlew test
# Uses H2 in-memory database — no MySQL needed for tests
```

---

## 🏗 Key Design Decisions

- **JWT stateless auth** – tokens expire in 24h, stored in localStorage
- **NHIF logic** – `paidByNHIF` is automatically set based on patient's insurance status at exam creation
- **Doctor edit restriction** – doctors can only edit/delete examinations they performed (checked in service layer via `SecurityContextHolder`)
- **Validation** – Bean Validation (`@Valid`) on all DTOs; global `@RestControllerAdvice` for structured error responses
- **Test DB** – `application-test.properties` uses H2 so tests run without MySQL
