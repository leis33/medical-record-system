import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { doctorApi, patientApi, examinationApi, sickLeaveApi, diagnosisApi } from '../services/api';

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ doctors: 0, patients: 0, examinations: 0, sickLeaves: 0, diagnoses: 0 });
  const [recentExams, setRecentExams] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const results = await Promise.allSettled([
          user.role !== 'PATIENT' ? doctorApi.getAll() : Promise.resolve({ data: [] }),
          user.role !== 'PATIENT' ? patientApi.getAll() : Promise.resolve({ data: [] }),
          examinationApi.getAll(),
          sickLeaveApi.getAll(),
          user.role !== 'PATIENT' ? diagnosisApi.getAll() : Promise.resolve({ data: [] }),
        ]);
        setStats({
          doctors: results[0].value?.data?.length || 0,
          patients: results[1].value?.data?.length || 0,
          examinations: results[2].value?.data?.length || 0,
          sickLeaves: results[3].value?.data?.length || 0,
          diagnoses: results[4].value?.data?.length || 0,
        });
        const exams = results[2].value?.data || [];
        setRecentExams(exams.slice(-5).reverse());
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [user.role]);

  if (loading) return <div className="loading-state"><div className="spinner" /></div>;

  return (
    <>
      <div className="page-header">
        <h1>Dashboard</h1>
        <span style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
          Welcome back, <strong>{user.username}</strong>
        </span>
      </div>
      <div className="page-body">
        <div className="stats-grid">
          {user.role !== 'PATIENT' && (
            <>
              <div className="stat-card blue">
                <div className="stat-label">Doctors</div>
                <div className="stat-value">{stats.doctors}</div>
              </div>
              <div className="stat-card teal">
                <div className="stat-label">Patients</div>
                <div className="stat-value">{stats.patients}</div>
              </div>
              <div className="stat-card green">
                <div className="stat-label">Diagnoses</div>
                <div className="stat-value">{stats.diagnoses}</div>
              </div>
            </>
          )}
          <div className="stat-card blue">
            <div className="stat-label">Examinations</div>
            <div className="stat-value">{stats.examinations}</div>
          </div>
          <div className="stat-card red">
            <div className="stat-label">Sick Leaves</div>
            <div className="stat-value">{stats.sickLeaves}</div>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <span className="card-title">Recent Examinations</span>
          </div>
          <div className="table-wrapper">
            {recentExams.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">📋</div>
                <h3>No examinations yet</h3>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Patient</th>
                    <th>Doctor</th>
                    <th>Diagnosis</th>
                    <th>Cost</th>
                    <th>Paid By</th>
                  </tr>
                </thead>
                <tbody>
                  {recentExams.map(exam => (
                    <tr key={exam.id}>
                      <td>{exam.date}</td>
                      <td>{exam.patientName}</td>
                      <td>{exam.doctorName}</td>
                      <td>{exam.diagnosisDescription || '—'}</td>
                      <td><strong>{exam.cost} BGN</strong></td>
                      <td>
                        <span className={`badge ${exam.paidByNHIF ? 'badge-success' : 'badge-warning'}`}>
                          {exam.paidByNHIF ? 'NHIF' : 'Patient'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
