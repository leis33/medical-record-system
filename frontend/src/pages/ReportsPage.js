import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { reportApi, diagnosisApi, doctorApi } from '../services/api';

function ReportTable({ columns, data, highlight }) {
  if (!data || data.length === 0) return <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', padding: '16px 0' }}>No data available.</p>;
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>{columns.map(c => <th key={c.key}>{c.label}</th>)}</tr>
        </thead>
        <tbody>
          {data.map((row, i) => (
            <tr key={i} className={highlight && i === 0 ? 'highlight-row' : ''}>
              {columns.map(c => <td key={c.key}>{c.render ? c.render(row) : row[c.key]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

const REPORTS = [
  { id: 'patientsByDiagnosis', label: 'Patients by Diagnosis', icon: '🔬', needsDiagnosis: true },
  { id: 'mostCommonDiagnoses', label: 'Most Common Diagnoses', icon: '📊' },
  { id: 'patientsByGP', label: 'Patients by GP', icon: '👨‍⚕️', needsDoctor: true },
  { id: 'totalPaidByPatients', label: 'Total Paid by Patients', icon: '💰' },
  { id: 'paidByPatientsByDoctor', label: 'Patient Payments by Doctor', icon: '💳' },
  { id: 'patientCountByGP', label: 'Patient Count per GP', icon: '👥' },
  { id: 'visitCountByDoctor', label: 'Visit Count by Doctor', icon: '📅' },
  { id: 'monthMostSickLeaves', label: 'Month with Most Sick Leaves', icon: '📄' },
  { id: 'doctorsMostSickLeaves', label: 'Doctors with Most Sick Leaves', icon: '🏥' },
  { id: 'patientHistory', label: 'Patient Visit History', icon: '📋', needsPatientId: true },
];

const MONTHS = ['', 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

export default function ReportsPage() {
  const [active, setActive] = useState('mostCommonDiagnoses');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [diagnoses, setDiagnoses] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [selectedDiag, setSelectedDiag] = useState('');
  const [selectedDoctor, setSelectedDoctor] = useState('');
  const [patientId, setPatientId] = useState('');

  useEffect(() => {
    diagnosisApi.getAll().then(r => setDiagnoses(r.data)).catch(() => {});
    doctorApi.getAll().then(r => setDoctors(r.data)).catch(() => {});
  }, []);

  const runReport = async () => {
    setLoading(true);
    setData(null);
    try {
      let res;
      switch (active) {
        case 'patientsByDiagnosis': res = await reportApi.patientsByDiagnosis(selectedDiag); break;
        case 'mostCommonDiagnoses': res = await reportApi.mostCommonDiagnoses(); break;
        case 'patientsByGP': res = await reportApi.patientsByGP(selectedDoctor); break;
        case 'totalPaidByPatients': res = await reportApi.totalPaidByPatients(); break;
        case 'paidByPatientsByDoctor': res = await reportApi.paidByPatientsByDoctor(); break;
        case 'patientCountByGP': res = await reportApi.patientCountByGP(); break;
        case 'visitCountByDoctor': res = await reportApi.visitCountByDoctor(); break;
        case 'monthMostSickLeaves': res = await reportApi.monthMostSickLeaves(); break;
        case 'doctorsMostSickLeaves': res = await reportApi.doctorsMostSickLeaves(); break;
        case 'patientHistory': res = await reportApi.patientHistory(patientId); break;
        default: break;
      }
      setData(res?.data);
    } catch (err) {
      toast.error(err.response?.data?.message || 'Report failed');
    } finally { setLoading(false); }
  };

  useEffect(() => { runReport(); }, [active]);

  const report = REPORTS.find(r => r.id === active);

  const renderResult = () => {
    if (loading) return <div className="loading-state"><div className="spinner" /></div>;
    if (data === null) return null;

    switch (active) {
      case 'patientsByDiagnosis':
      case 'patientsByGP':
        return <ReportTable columns={[
          { key: 'name', label: 'Patient' },
          { key: 'personalIdentificationNumber', label: 'EGN' },
          { key: 'personalDoctorName', label: 'Personal Doctor' },
          { key: 'hasHealthInsurance', label: 'Insurance', render: r => <span className={`badge ${r.hasHealthInsurance ? 'badge-success' : 'badge-danger'}`}>{r.hasHealthInsurance ? 'Insured' : 'Uninsured'}</span> },
        ]} data={data} />;

      case 'mostCommonDiagnoses':
        return <ReportTable highlight columns={[
          { key: 'code', label: 'Code', render: r => <span className="badge badge-primary">{r.diagnosis?.code}</span> },
          { key: 'description', label: 'Description', render: r => r.diagnosis?.description },
          { key: 'count', label: 'Count', render: r => <strong>{String(r.count)}</strong> },
        ]} data={data} />;

      case 'totalPaidByPatients':
        return (
          <div style={{ padding: '24px 0' }}>
            <div style={{ fontSize: '0.88rem', color: 'var(--text-secondary)', marginBottom: 8 }}>Total examination costs paid directly by patients (no insurance)</div>
            <div style={{ fontSize: '2.5rem', fontWeight: 700, fontFamily: 'IBM Plex Mono', color: 'var(--text-primary)' }}>
              {Number(data.total || 0).toFixed(2)} <span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>BGN</span>
            </div>
          </div>
        );

      case 'paidByPatientsByDoctor':
        return <ReportTable highlight columns={[
          { key: 'doctor', label: 'Doctor', render: r => r.doctor?.name },
          { key: 'specialty', label: 'Specialty', render: r => r.doctor?.specialty },
          { key: 'total', label: 'Amount Paid by Patients', render: r => <strong>{Number(r.total).toFixed(2)} BGN</strong> },
        ]} data={data} />;

      case 'patientCountByGP':
        return <ReportTable highlight columns={[
          { key: 'doctor', label: 'GP Name', render: r => r.doctor?.name },
          { key: 'specialty', label: 'Specialty', render: r => r.doctor?.specialty },
          { key: 'patientCount', label: 'Registered Patients', render: r => <strong>{String(r.patientCount)}</strong> },
        ]} data={data} />;

      case 'visitCountByDoctor':
        return <ReportTable highlight columns={[
          { key: 'doctor', label: 'Doctor', render: r => r.doctor?.name },
          { key: 'specialty', label: 'Specialty', render: r => r.doctor?.specialty },
          { key: 'visitCount', label: 'Total Visits', render: r => <strong>{String(r.visitCount)}</strong> },
        ]} data={data} />;

      case 'monthMostSickLeaves':
        return <ReportTable highlight columns={[
          { key: 'month', label: 'Month', render: r => MONTHS[r.month] || r.month },
          { key: 'year', label: 'Year', render: r => String(r.year) },
          { key: 'count', label: 'Sick Leaves Issued', render: r => <strong>{String(r.count)}</strong> },
        ]} data={data} />;

      case 'doctorsMostSickLeaves':
        return <ReportTable highlight columns={[
          { key: 'doctor', label: 'Doctor', render: r => r.doctor?.name },
          { key: 'specialty', label: 'Specialty', render: r => r.doctor?.specialty },
          { key: 'sickLeaveCount', label: 'Sick Leaves Issued', render: r => <strong>{String(r.sickLeaveCount)}</strong> },
        ]} data={data} />;

      case 'patientHistory':
        return <ReportTable columns={[
          { key: 'date', label: 'Date' },
          { key: 'doctorName', label: 'Doctor' },
          { key: 'diagnosisDescription', label: 'Diagnosis', render: r => r.diagnosisDescription
            ? <span className="badge badge-primary">{r.diagnosisDescription}</span> : '—' },
          { key: 'prescribedTreatment', label: 'Treatment' },
          { key: 'cost', label: 'Cost', render: r => `${Number(r.cost).toFixed(2)} BGN` },
          { key: 'paidByNHIF', label: 'Paid By', render: r => <span className={`badge ${r.paidByNHIF ? 'badge-success' : 'badge-warning'}`}>{r.paidByNHIF ? 'NHIF' : 'Patient'}</span> },
        ]} data={data} />;

      default: return null;
    }
  };

  return (
    <>
      <div className="page-header">
        <h1>Reports & Statistics</h1>
      </div>
      <div className="page-body">
        <div style={{ display: 'grid', gridTemplateColumns: '220px 1fr', gap: 20, alignItems: 'start' }}>
          {/* Report menu */}
          <div className="card">
            <div className="card-body" style={{ padding: '8px' }}>
              {REPORTS.map(r => (
                <button key={r.id} onClick={() => { setActive(r.id); setData(null); }}
                  style={{
                    display: 'flex', alignItems: 'center', gap: 8, width: '100%',
                    padding: '10px 12px', border: 'none', borderRadius: 6, cursor: 'pointer',
                    background: active === r.id ? 'var(--primary-light)' : 'transparent',
                    color: active === r.id ? 'var(--primary)' : 'var(--text-primary)',
                    fontWeight: active === r.id ? 600 : 400,
                    fontSize: '0.85rem', textAlign: 'left', marginBottom: 2,
                    transition: 'all 0.15s',
                  }}>
                  <span>{r.icon}</span> {r.label}
                </button>
              ))}
            </div>
          </div>

          {/* Report content */}
          <div className="card">
            <div className="card-header">
              <span className="card-title">{report?.icon} {report?.label}</span>
              <button className="btn btn-primary btn-sm" onClick={runReport} disabled={loading}>
                {loading ? 'Running…' : '▶ Run Report'}
              </button>
            </div>
            <div className="card-body">
              {/* Parameters */}
              {report?.needsDiagnosis && (
                <div style={{ marginBottom: 16, display: 'flex', gap: 12, alignItems: 'center' }}>
                  <select className="form-control" style={{ maxWidth: 320 }} value={selectedDiag}
                    onChange={e => setSelectedDiag(e.target.value)}>
                    <option value="">— Select diagnosis —</option>
                    {diagnoses.map(d => <option key={d.id} value={d.id}>{d.code} – {d.description}</option>)}
                  </select>
                  <button className="btn btn-primary btn-sm" onClick={runReport} disabled={!selectedDiag}>Run</button>
                </div>
              )}
              {report?.needsDoctor && (
                <div style={{ marginBottom: 16, display: 'flex', gap: 12, alignItems: 'center' }}>
                  <select className="form-control" style={{ maxWidth: 320 }} value={selectedDoctor}
                    onChange={e => setSelectedDoctor(e.target.value)}>
                    <option value="">— Select doctor —</option>
                    {doctors.filter(d => d.canBePersonalDoctor).map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                  </select>
                  <button className="btn btn-primary btn-sm" onClick={runReport} disabled={!selectedDoctor}>Run</button>
                </div>
              )}
              {report?.needsPatientId && (
                <div style={{ marginBottom: 16, display: 'flex', gap: 12, alignItems: 'center' }}>
                  <input className="form-control" style={{ maxWidth: 200 }} placeholder="Patient ID" type="number"
                    value={patientId} onChange={e => setPatientId(e.target.value)} />
                  <button className="btn btn-primary btn-sm" onClick={runReport} disabled={!patientId}>Run</button>
                </div>
              )}
              {renderResult()}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
