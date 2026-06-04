import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { diagnosisApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ConfirmDialog from '../components/common/ConfirmDialog';

function DiagnosisModal({ diagnosis, onClose, onSaved }) {
  const [form, setForm] = useState(diagnosis || { code: '', description: '' });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.code) e.code = 'Required';
    if (!form.description) e.description = 'Required';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    try {
      if (form.id) await diagnosisApi.update(form.id, form);
      else await diagnosisApi.create(form);
      toast.success(`Diagnosis ${form.id ? 'updated' : 'created'}`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Operation failed');
    } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" style={{ maxWidth: 480 }} onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{form.id ? 'Edit Diagnosis' : 'Add Diagnosis'}</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div className="form-group">
                <label className="form-label required">ICD Code</label>
                <input className={`form-control ${errors.code ? 'error' : ''}`}
                  value={form.code} onChange={e => setForm({ ...form, code: e.target.value })}
                  placeholder="e.g. J06, I10, K29" />
                {errors.code && <span className="form-error">{errors.code}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Description</label>
                <textarea className={`form-control ${errors.description ? 'error' : ''}`}
                  value={form.description} onChange={e => setForm({ ...form, description: e.target.value })}
                  placeholder="Diagnosis description" rows={3} />
                {errors.description && <span className="form-error">{errors.description}</span>}
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

export default function DiagnosesPage() {
  const { user } = useAuth();
  const [diagnoses, setDiagnoses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editDiag, setEditDiag] = useState(null);
  const [deleteId, setDeleteId] = useState(null);

  const load = async () => {
    try { const res = await diagnosisApi.getAll(); setDiagnoses(res.data); }
    catch { toast.error('Failed to load diagnoses'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = diagnoses.filter(d =>
    d.code.toLowerCase().includes(search.toLowerCase()) ||
    d.description.toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async () => {
    try { await diagnosisApi.delete(deleteId); toast.success('Deleted'); setDeleteId(null); load(); }
    catch { toast.error('Failed to delete'); }
  };

  const canEdit = user?.role === 'ADMIN' || user?.role === 'DOCTOR';
  const canDelete = user?.role === 'ADMIN';

  return (
    <>
      <div className="page-header">
        <h1>Diagnoses</h1>
        <div className="search-bar">
          <div className="search-input-wrap">
            <span className="search-icon">🔍</span>
            <input placeholder="Search by code or description…" value={search} onChange={e => setSearch(e.target.value)} />
          </div>
          {canEdit && (
            <button className="btn btn-primary" onClick={() => { setEditDiag(null); setShowModal(true); }}>
              + Add Diagnosis
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
                  <div className="empty-icon">🔬</div>
                  <h3>No diagnoses found</h3>
                </div>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>ICD Code</th>
                      <th>Description</th>
                      <th>Usage Count</th>
                      {(canEdit || canDelete) && <th style={{ width: 100 }}>Actions</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map(d => (
                      <tr key={d.id}>
                        <td><span className="badge badge-primary">{d.code}</span></td>
                        <td>{d.description}</td>
                        <td>{d.examinationCount}</td>
                        {(canEdit || canDelete) && (
                          <td>
                            <div style={{ display: 'flex', gap: 4 }}>
                              {canEdit && <button className="btn-icon" onClick={() => { setEditDiag(d); setShowModal(true); }}>✏️</button>}
                              {canDelete && <button className="btn-icon danger" onClick={() => setDeleteId(d.id)}>🗑️</button>}
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

      {showModal && <DiagnosisModal diagnosis={editDiag} onClose={() => setShowModal(false)} onSaved={() => { setShowModal(false); load(); }} />}
      <ConfirmDialog isOpen={!!deleteId} title="Delete Diagnosis" message="Delete this diagnosis?" onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />
    </>
  );
}
