import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { examinationApi, doctorApi, patientApi, diagnosisApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ConfirmDialog from '../components/common/ConfirmDialog';

function ExamModal({ exam, doctors, patients, diagnoses, onClose, onSaved, currentUser }) {
  const isDoctor = currentUser?.role === 'DOCTOR';
  const doctorProfileId = currentUser?.profileId;

  const [form, setForm] = useState(exam || {
    date: new Date().toISOString().split('T')[0],
    doctorId: isDoctor ? doctorProfileId : '',
    patientId: '',
    diagnosisId: '',
    prescribedTreatment: '',
    cost: '',
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.date) e.date = 'Required';
    if (!form.doctorId) e.doctorId = 'Required';
    if (!form.patientId) e.patientId = 'Required';
    if (!form.prescribedTreatment) e.prescribedTreatment = 'Required';
    if (!form.cost && form.cost !== 0) e.cost = 'Required';
    else if (isNaN(form.cost) || Number(form.cost) < 0) e.cost = 'Must be a non-negative number';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    try {
      const payload = {
        ...form,
        doctorId: Number(form.doctorId),
        patientId: Number(form.patientId),
        diagnosisId: form.diagnosisId ? Number(form.diagnosisId) : null,
        cost: Number(form.cost),
      };
      if (form.id) await examinationApi.update(form.id, payload);
      else await examinationApi.create(payload);
      toast.success(`Examination ${form.id ? 'updated' : 'created'}`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Operation failed');
    } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal modal-lg" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{form.id ? 'Edit Examination' : 'New Examination'}</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label required">Date</label>
                <input type="date" className={`form-control ${errors.date ? 'error' : ''}`}
                  value={form.date} onChange={e => setForm({ ...form, date: e.target.value })} />
                {errors.date && <span className="form-error">{errors.date}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Doctor</label>
                <select className={`form-control ${errors.doctorId ? 'error' : ''}`}
                  value={form.doctorId} onChange={e => setForm({ ...form, doctorId: e.target.value })}
                  disabled={isDoctor}>
                  <option value="">— Select doctor —</option>
                  {doctors.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                </select>
                {errors.doctorId && <span className="form-error">{errors.doctorId}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Patient</label>
                <select className={`form-control ${errors.patientId ? 'error' : ''}`}
                  value={form.patientId} onChange={e => setForm({ ...form, patientId: e.target.value })}>
                  <option value="">— Select patient —</option>
                  {patients.map(p => <option key={p.id} value={p.id}>{p.name} ({p.personalIdentificationNumber})</option>)}
                </select>
                {errors.patientId && <span className="form-error">{errors.patientId}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">Diagnosis</label>
                <select className="form-control" value={form.diagnosisId || ''}
                  onChange={e => setForm({ ...form, diagnosisId: e.target.value || null })}>
                  <option value="">— Select diagnosis —</option>
                  {diagnoses.map(d => <option key={d.id} value={d.id}>{d.code} – {d.description}</option>)}
                </select>
              </div>
              <div className="form-group form-full">
                <label className="form-label required">Prescribed Treatment</label>
                <textarea className={`form-control ${errors.prescribedTreatment ? 'error' : ''}`}
                  value={form.prescribedTreatment}
                  onChange={e => setForm({ ...form, prescribedTreatment: e.target.value })}
                  rows={3} placeholder="Treatment details…" />
                {errors.prescribedTreatment && <span className="form-error">{errors.prescribedTreatment}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Cost (BGN)</label>
                <input type="number" step="0.01" min="0" className={`form-control ${errors.cost ? 'error' : ''}`}
                  value={form.cost} onChange={e => setForm({ ...form, cost: e.target.value })}
                  placeholder="0.00" />
                {errors.cost && <span className="form-error">{errors.cost}</span>}
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function ExaminationsPage() {
  const { user } = useAuth();
  const [exams, setExams] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [diagnoses, setDiagnoses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editExam, setEditExam] = useState(null);
  const [deleteId, setDeleteId] = useState(null);

  const load = async () => {
    try {
      const calls = [examinationApi.getAll(), doctorApi.getAll(), diagnosisApi.getAll()];
      if (user?.role !== 'PATIENT') calls.push(patientApi.getAll());
      const results = await Promise.allSettled(calls);
      setExams(results[0].value?.data || []);
      setDoctors(results[1].value?.data || []);
      setDiagnoses(results[2].value?.data || []);
      if (user?.role !== 'PATIENT') setPatients(results[3].value?.data || []);
    } catch { toast.error('Failed to load examinations'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = exams.filter(e =>
    (e.patientName || '').toLowerCase().includes(search.toLowerCase()) ||
    (e.doctorName || '').toLowerCase().includes(search.toLowerCase()) ||
    (e.diagnosisDescription || '').toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    try { await examinationApi.delete(deleteId); toast.success('Deleted'); setDeleteId(null); load(); }
    catch { toast.error('Failed to delete'); }
  };

  const canEdit = user?.role === 'ADMIN' || user?.role === 'DOCTOR';

  return (
    <>
      <div className="page-header">
        <h1>Examinations</h1>
        <div className="search-bar">
          <div className="search-input-wrap">
            <span className="search-icon">🔍</span>
            <input placeholder="Search examinations…" value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          {canEdit && (
            <button className="btn btn-primary" onClick={() => { setEditExam(null); setShowModal(true); }}>
              + New Examination
            </button>
          )}
        </div>
      </div>

      <div className="page-body">
        <div className="card">
          <div className="table-wrapper">
            {loading ? <div className="loading-state"><div className="spinner" /></div>
              : filtered.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">📋</div>
                  <h3>No examinations found</h3>
                </div>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Patient</th>
                      <th>Doctor</th>
                      <th>Diagnosis</th>
                      <th>Treatment</th>
                      <th>Cost</th>
                      <th>Paid By</th>
                      {canEdit && <th style={{ width: 100 }}>Actions</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map(e => (
                      <tr key={e.id}>
                        <td>{e.date}</td>
                        <td><strong>{e.patientName}</strong></td>
                        <td>{e.doctorName}</td>
                        <td>{e.diagnosisDescription
                          ? <span className="badge badge-primary">{e.diagnosisDescription}</span>
                          : <span style={{ color: 'var(--text-muted)' }}>—</span>}
                        </td>
                        <td style={{ maxWidth: 200 }}>
                          <span style={{ display: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                            {e.prescribedTreatment}
                          </span>
                        </td>
                        <td><strong>{Number(e.cost).toFixed(2)} BGN</strong></td>
                        <td><span className={`badge ${e.paidByNHIF ? 'badge-success' : 'badge-warning'}`}>
                          {e.paidByNHIF ? 'NHIF' : 'Patient'}
                        </span></td>
                        {canEdit && (
                          <td>
                            <div style={{ display: 'flex', gap: 4 }}>
                              <button className="btn-icon" onClick={() => { setEditExam(e); setShowModal(true); }}>✏️</button>
                              <button className="btn-icon danger" onClick={() => setDeleteId(e.id)}>🗑️</button>
                            </div>
                          </td>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
          </div>
        </div>
      </div>

      {showModal && (
        <ExamModal exam={editExam} doctors={doctors} patients={patients} diagnoses={diagnoses}
          onClose={() => setShowModal(false)} onSaved={() => { setShowModal(false); load(); }} currentUser={user} />
      )}
      <ConfirmDialog isOpen={!!deleteId} title="Delete Examination" message="Delete this examination record?"
        onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />
    </>
  );
}
