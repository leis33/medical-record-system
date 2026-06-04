import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export default api;

// Doctors
export const doctorApi = {
  getAll: () => api.get('/doctors'),
  getById: (id) => api.get(`/doctors/${id}`),
  getPersonalDoctors: () => api.get('/doctors/personal-doctors'),
  create: (data) => api.post('/doctors', data),
  update: (id, data) => api.put(`/doctors/${id}`, data),
  delete: (id) => api.delete(`/doctors/${id}`),
};

// Patients
export const patientApi = {
  getAll: () => api.get('/patients'),
  getById: (id) => api.get(`/patients/${id}`),
  getByDoctor: (docId) => api.get(`/patients/by-doctor/${docId}`),
  create: (data) => api.post('/patients', data),
  update: (id, data) => api.put(`/patients/${id}`, data),
  delete: (id) => api.delete(`/patients/${id}`),
};

// Diagnoses
export const diagnosisApi = {
  getAll: () => api.get('/diagnoses'),
  getById: (id) => api.get(`/diagnoses/${id}`),
  create: (data) => api.post('/diagnoses', data),
  update: (id, data) => api.put(`/diagnoses/${id}`, data),
  delete: (id) => api.delete(`/diagnoses/${id}`),
};

// Examinations
export const examinationApi = {
  getAll: () => api.get('/examinations'),
  getById: (id) => api.get(`/examinations/${id}`),
  getByPatient: (pid) => api.get(`/examinations/patient/${pid}`),
  getByDoctor: (did) => api.get(`/examinations/doctor/${did}`),
  getByDoctorAndPeriod: (did, from, to) => api.get(`/examinations/doctor/${did}/period?from=${from}&to=${to}`),
  getByPeriod: (from, to) => api.get(`/examinations/period?from=${from}&to=${to}`),
  create: (data) => api.post('/examinations', data),
  update: (id, data) => api.put(`/examinations/${id}`, data),
  delete: (id) => api.delete(`/examinations/${id}`),
};

// Sick leaves
export const sickLeaveApi = {
  getAll: () => api.get('/sick-leaves'),
  getById: (id) => api.get(`/sick-leaves/${id}`),
  getByPatient: (pid) => api.get(`/sick-leaves/patient/${pid}`),
  create: (data) => api.post('/sick-leaves', data),
  update: (id, data) => api.put(`/sick-leaves/${id}`, data),
  delete: (id) => api.delete(`/sick-leaves/${id}`),
};

// Reports
export const reportApi = {
  patientsByDiagnosis: (did) => api.get(`/reports/patients-by-diagnosis/${did}`),
  mostCommonDiagnoses: () => api.get('/reports/most-common-diagnoses'),
  patientsByGP: (did) => api.get(`/reports/patients-by-gp/${did}`),
  totalPaidByPatients: () => api.get('/reports/total-paid-by-patients'),
  paidByPatientsByDoctor: () => api.get('/reports/paid-by-patients-per-doctor'),
  patientCountByGP: () => api.get('/reports/patient-count-by-gp'),
  visitCountByDoctor: () => api.get('/reports/visit-count-by-doctor'),
  monthMostSickLeaves: () => api.get('/reports/month-most-sick-leaves'),
  doctorsMostSickLeaves: () => api.get('/reports/doctors-most-sick-leaves'),
  patientHistory: (pid) => api.get(`/reports/patient-history/${pid}`),
};
