import React, { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { sickLeaveApi, examinationApi } from '../services/api';
import { useAuth } from '../context/AuthContext';
import ConfirmDialog from '../components/common/ConfirmDialog';

function SickLeaveModal({ sickLeave, examinations, onClose, onSaved }) {
  const [form, setForm] = useState(sickLeave || {
    startDate: new Date().toISOString().split('T')[0],
    numberOfDays: 1,
    examinationId: '',
  });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const e = {};
    if (!form.startDate) e.startDate = 'Required';
    if (!form.numberOfDays || form.numberOfDays < 1) e.numberOfDays = 'Must be at least 1';
    if (!form.examinationId) e.examinationId = 'Required';
    return e;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSaving(true);
    try {
      const payload = { ...form, examinationId: Number(form.examinationId), numberOfDays: Number(form.numberOfDays) };
      if (form.id) await sickLeaveApi.update(form.id, payload);
      else await sickLeaveApi.create(payload);
      toast.success(`Sick leave ${form.id ? 'updated' : 'created'}`);
      onSaved();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Operation failed');
    } finally { setSaving(false); }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" style={{ maxWidth: 480 }} onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">{form.id ? 'Edit Sick Leave' : 'Issue Sick Leave'}</h2>
          <button className="close-btn" onClick={onClose}>✕</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              <div className="form-group">
                <label className="form-label required">Linked Examination</label>
                <select className={`form-control ${errors.examinationId ? 'error' : ''}`}
                  value={form.examinationId} onChange={e => setForm({ ...form, examinationId: e.target.value })}>
                  <option value="">— Select examination —</option>
                  {examinations.map(ex => (
                    <option key={ex.id} value={ex.id}>
                      {ex.date} – {ex.patientName} ({ex.doctorName})
                    </option>
                  ))}
                </select>
                {errors.examinationId && <span className="form-error">{errors.examinationId}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Start Date</label>
                <input type="date" className={`form-control ${errors.startDate ? 'error' : ''}`}
                  value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value })} />
                {errors.startDate && <span className="form-error">{errors.startDate}</span>}
              </div>
              <div className="form-group">
                <label className="form-label required">Number of Days</label>
                <input type="number" min="1" className={`form-control ${errors.numberOfDays ? 'error' : ''}`}
                  value={form.numberOfDays} onChange={e => setForm({ ...form, numberOfDays: e.target.value })} />
                {errors.numberOfDays && <span className="form-error">{errors.numberOfDays}</span>}
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

export default function SickLeavesPage() {
  const { user } = useAuth();
  const [sickLeaves, setSickLeaves] = useState([]);
  const [examinations, setExaminations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [deleteId, setDeleteId] = useState(null);

  const load = async () => {
    try {
      const [slRes, exRes] = await Promise.all([sickLeaveApi.getAll(), examinationApi.getAll()]);
      setSickLeaves(slRes.data);
      setExaminations(exRes.data);
    } catch { toast.error('Failed to load sick leaves'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async () => {
    try { await sickLeaveApi.delete(deleteId); toast.success('Deleted'); setDeleteId(null); load(); }
    catch { toast.error('Failed to delete'); }
  };

  const canEdit = user?.role === 'ADMIN' || user?.role === 'DOCTOR';

  // Enrich sick leaves with examination details
  const enriched = sickLeaves.map(sl => {
    const exam = examinations.find(e => e.id === sl.examinationId);
    return { ...sl, examDate: exam?.date, patientName: exam?.patientName, doctorName: exam?.doctorName };
  });

  const endDate = (startDate, days) => {
    if (!startDate) return '—';
    const d = new Date(startDate);
    d.setDate(d.getDate() + days - 1);
    return d.toISOString().split('T')[0];
  };

  return (
    <>
      <div className="page-header">
        <h1>Sick Leaves</h1>
        {canEdit && (
          <button className="btn btn-primary" onClick={() => { setEditItem(null); setShowModal(true); }}>
            + Issue Sick Leave
          </button>
        )}
      </div>

      <div className="page-body">
        <div className="card">
          <div className="table-wrapper">
            {loading ? <div className="loading-state"><div className="spinner" /></div>
              : enriched.length === 0 ? (
                <div className="empty-state">
                  <div className="empty-icon">📄</div>
                  <h3>No sick leaves issued</h3>
                </div>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Patient</th>
                      <th>Doctor</th>
                      <th>Exam Date</th>
                      <th>Start Date</th>
                      <th>Days</th>
                      <th>End Date</th>
                      {canEdit && <th style={{ width: 100 }}>Actions</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {enriched.map(sl => (
                      <tr key={sl.id}>
                        <td><strong>{sl.patientName || '—'}</strong></td>
                        <td>{sl.doctorName || '—'}</td>
                        <td>{sl.examDate || '—'}</td>
                        <td>{sl.startDate}</td>
                        <td><span className="badge badge-warning">{sl.numberOfDays} days</span></td>
                        <td>{endDate(sl.startDate, sl.numberOfDays)}</td>
                        {canEdit && (
                          <td>
                            <div style={{ display: 'flex', gap: 4 }}>
                              <button className="btn-icon" onClick={() => { setEditItem(sl); setShowModal(true); }}>✏️</button>
                              <button className="btn-icon danger" onClick={() => setDeleteId(sl.id)}>🗑️</button>
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
        <SickLeaveModal sickLeave={editItem} examinations={examinations}
          onClose={() => setShowModal(false)} onSaved={() => { setShowModal(false); load(); }} />
      )}
      <ConfirmDialog isOpen={!!deleteId} title="Delete Sick Leave" message="Delete this sick leave record?"
        onConfirm={handleDelete} onCancel={() => setDeleteId(null)} />
    </>
  );
}
