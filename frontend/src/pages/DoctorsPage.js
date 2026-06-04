import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { doctorApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ConfirmDialog from '../components/common/ConfirmDialog';

function DoctorModal({ doctor, onClose, onSaved }) {
  const [form, setForm] = useState(doctor || {
    identificationNumber: '', name: '', specialty: '', canBePersonalDoctor: false
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.identificationNumber) e.identificationNumber = 'Required';
    if (!form.name) e.name = 'Required';
    if (!form.specialty) e.specialty = 'Required';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    try {
      if (form.id) await doctorApi.update(form.id, form);
      else await doctorApi.create(form);
      toast.success(`Doctor ${form.id ? 'updated' : 'created'} successfully`);
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
          <h2 className="modal-title">{form.id ? 'Edit Doctor' : 'Add Doctor'}</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-grid">
              <div className="form-group">
                <label className="form-label required">ID Number</label>
                <input className={`form-control ${errors.identificationNumber ? 'error' : ''}`}
                  value={form.identificationNumber}
                  onChange={e => setForm({ ...form, identificationNumber: e.target.value })}
                  placeholder="e.g. DOC001" />
                {errors.identificationNumber && <span className="form-error">{errors.identificationNumber}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Full Name</label>
                <input className={`form-control ${errors.name ? 'error' : ''}`}
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                  placeholder="Dr. First Last" />
                {errors.name && <span className="form-error">{errors.name}</span>}
              </div>
              <div className="form-group form-full">
                <label className="form-label required">Specialty</label>
                <input className={`form-control ${errors.specialty ? 'error' : ''}`}
                  value={form.specialty}
                  onChange={e => setForm({ ...form, specialty: e.target.value })}
                  placeholder="e.g. General Practice, Cardiology" />
                {errors.specialty && <span className="form-error">{errors.specialty}</span>}
              </div>
              <div className="form-group form-full">
                <label className="form-check">
                  <input type="checkbox" checked={form.canBePersonalDoctor}
                    onChange={e => setForm({ ...form, canBePersonalDoctor: e.target.checked })} />
                  <span>Can be a personal (GP) doctor</span>
                </label>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : 'Save Doctor'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function DoctorsPage() {
  const { user } = useAuth();
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [modalDoctor, setModalDoctor] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [deleteId, setDeleteId] = useState(null);

  const load = async () => {
    try {
      const res = await doctorApi.getAll();
      setDoctors(res.data);
    } catch { toast.error('Failed to load doctors'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = doctors.filter(d =>
    d.name.toLowerCase().includes(search.toLowerCase()) ||
    d.identificationNumber.toLowerCase().includes(search.toLowerCase()) ||
    d.specialty.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    try {
      await doctorApi.delete(deleteId);
      toast.success('Doctor deleted');
      setDeleteId(null);
      load();
    } catch { toast.error('Failed to delete doctor'); }
  };

  const isAdmin = user?.role === 'ADMIN';

  return (
    <>
      <div className="page-header">
        <h1>Doctors</h1>
        <div className="search-bar">
          <div className="search-input-wrap">
            <span className="search-icon">🔍</span>
            <input placeholder="Search doctors…" value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          {isAdmin && (
            <button className="btn btn-primary" onClick={() => { setModalDoctor(null); setShowModal(true); }}>
              + Add Doctor
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
                <div className="empty-icon">👨‍⚕️</div>
                <h3>No doctors found</h3>
                <p>{search ? 'Try a different search term' : 'Add your first doctor'}</p>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>ID Number</th>
                    <th>Name</th>
                    <th>Specialty</th>
                    <th>GP Status</th>
                    <th>Patients</th>
                    {isAdmin && <th style={{ width: 100 }}>Actions</th>}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map(doc => (
                    <tr key={doc.id}>
                      <td><code style={{ fontSize: '0.82rem', background: '#f0f4ff', padding: '2px 6px', borderRadius: 4 }}>{doc.identificationNumber}</code></td>
                      <td><strong>{doc.name}</strong></td>
                      <td>{doc.specialty}</td>
                      <td>
                        <span className={`badge ${doc.canBePersonalDoctor ? 'badge-success' : 'badge-secondary'}`}>
                          {doc.canBePersonalDoctor ? 'GP Eligible' : 'Specialist'}
                        </span>
                      </td>
                      <td>{doc.patientCount}</td>
                      {isAdmin && (
                        <td>
                          <div style={{ display: 'flex', gap: 4 }}>
                            <button className="btn-icon" title="Edit" onClick={() => { setModalDoctor(doc); setShowModal(true); }}>✏️</button>
                            <button className="btn-icon danger" title="Delete" onClick={() => setDeleteId(doc.id)}>🗑️</button>
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
        <DoctorModal
          doctor={modalDoctor}
          onClose={() => setShowModal(false)}
          onSaved={() => { setShowModal(false); load(); }}
        />
      )}

      <ConfirmDialog
        isOpen={!!deleteId}
        title="Delete Doctor"
        message="Are you sure you want to delete this doctor? This action cannot be undone."
        onConfirm={handleDelete}
        onCancel={() => setDeleteId(null)}
      />
    </>
  );
}
