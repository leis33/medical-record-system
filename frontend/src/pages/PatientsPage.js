import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { patientApi, doctorApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ConfirmDialog from '../components/common/ConfirmDialog';

function PatientModal({ patient, doctors, onClose, onSaved }) {
  const [form, setForm] = useState(patient || {
    name: '', personalIdentificationNumber: '', personalDoctorId: '', hasHealthInsurance: false
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.name) e.name = 'Required';
    if (!form.personalIdentificationNumber) e.personalIdentificationNumber = 'Required';
    else if (!/^\d{10}$/.test(form.personalIdentificationNumber)) e.personalIdentificationNumber = 'Must be exactly 10 digits';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    try {
      const payload = { ...form, personalDoctorId: form.personalDoctorId || null };
      if (form.id) await patientApi.update(form.id, payload);
      else await patientApi.create(payload);
      toast.success(`Patient ${form.id ? 'updated' : 'created'} successfully`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Operation failed');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{form.id ? 'Edit Patient' : 'Add Patient'}</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label required">Full Name</label>
                <input className={`form-control ${errors.name ? 'error' : ''}`}
                  value={form.name} onChange={e => setForm({ ...form, name: e.target.value })}
                  placeholder="First Last" />
                {errors.name && <span className="form-error">{errors.name}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Personal ID Number (EGN)</label>
                <input className={`form-control ${errors.personalIdentificationNumber ? 'error' : ''}`}
                  value={form.personalIdentificationNumber}
                  onChange={e => setForm({ ...form, personalIdentificationNumber: e.target.value })}
                  placeholder="10-digit EGN" maxLength={10} />
                {errors.personalIdentificationNumber && <span className="form-error">{errors.personalIdentificationNumber}</span>}
              </div>
              <div className="form-group form-full">
                <label className="form-label">Personal Doctor (GP)</label>
                <select className="form-control" value={form.personalDoctorId || ''}
                  onChange={e => setForm({ ...form, personalDoctorId: e.target.value || null })}>
                  <option value="">— No personal doctor —</option>
                  {doctors.filter(d => d.canBePersonalDoctor).map(d => (
                    <option key={d.id} value={d.id}>{d.name} ({d.specialty})</option>
                  ))}
                </select>
              </div>
              <div className="form-group form-full">
                <label className="form-check">
                  <input type="checkbox" checked={form.hasHealthInsurance}
                    onChange={e => setForm({ ...form, hasHealthInsurance: e.target.checked })} />
                  <span>Has valid health insurance (last 6 months)</span>
                </label>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save Patient'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function PatientsPage() {
  const { user } = useAuth();
  const [patients, setPatients] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editPatient, setEditPatient] = useState(null);
  const [deleteId, setDeleteId] = useState(null);

  const load = async () => {
    try {
      const [pRes, dRes] = await Promise.all([patientApi.getAll(), doctorApi.getAll()]);
      setPatients(pRes.data);
      setDoctors(dRes.data);
    } catch { toast.error('Failed to load patients'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = patients.filter(p =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    p.personalIdentificationNumber.includes(search) ||
    (p.personalDoctorName || '').toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    try {
      await patientApi.delete(deleteId);
      toast.success('Patient deleted');
      setDeleteId(null);
      load();
    } catch { toast.error('Failed to delete patient'); }
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <>
      <div className="page-header">
        <h1>Patients</h1>
        <div className="search-bar">
          <div className="search-input-wrap">
            <span className="search-icon">🔍</span>
            <input placeholder="Search patients…" value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          {isAdmin && (
            <button className="btn btn-primary" onClick={() => { setEditPatient(null); setShowModal(true); }}>
              + Add Patient
            </button>
          )}
        </div>
      </div>

      <div className="page-body">
        <div className="card">
          <div className="table-wrapper">
            {loading ? (
              <div className="loading-state"><div className="spinner" /></div>
            ) : filtered.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">🏥</div>
                <h3>No patients found</h3>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>EGN</th>
                    <th>Name</th>
                    <th>Personal Doctor</th>
                    <th>Insurance</th>
                    <th>Examinations</th>
                    {isAdmin && <th style={{ width: 100 }}>Actions</th>}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map(p => (
                    <tr key={p.id}>
                      <td><code style={{ fontSize: '0.82rem', background: '#f0f4ff', padding: '2px 6px', borderRadius: 4 }}>{p.personalIdentificationNumber}</code></td>
                      <td><strong>{p.name}</strong></td>
                      <td>{p.personalDoctorName || <span style={{ color: 'var(--text-muted)' }}>—</span>}</td>
                      <td>
                        <span className={`badge ${p.hasHealthInsurance ? 'badge-success' : 'badge-danger'}`}>
                          {p.hasHealthInsurance ? '✓ Insured' : '✗ Uninsured'}
                        </span>
                      </td>
                      <td>{p.examinationCount}</td>
                      {isAdmin && (
                        <td>
                          <div style={{ display: 'flex', gap: 4 }}>
                            <button className="btn-icon" onClick={() => { setEditPatient(p); setShowModal(true); }}>✏️</button>
                            <button className="btn-icon danger" onClick={() => setDeleteId(p.id)}>🗑️</button>
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
        <PatientModal
          patient={editPatient}
          doctors={doctors}
          onClose={() => setShowModal(false)}
          onSaved={() => { setShowModal(false); load(); }}
        />
      )}

      <ConfirmDialog isOpen={!!deleteId} title="Delete Patient"
        message="Delete this patient? All their examinations will also be affected."
        onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />
    </>
  );
}
