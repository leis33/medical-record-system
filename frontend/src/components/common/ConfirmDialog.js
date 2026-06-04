import React from 'react';

export default function ConfirmDialog({ isOpen, title, message, onConfirm, onCancel, danger = true }) {
  if (!isOpen) return null;
  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal confirm-dialog" onClick={e => e.stopPropagation()}>
        <div className="modal-body">
          <div className="confirm-icon">{danger ? '⚠️' : 'ℹ️'}</div>
          <h3>{title}</h3>
          <p>{message}</p>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onCancel}>Cancel</button>
          <button className={`btn ${danger ? 'btn-danger' : 'btn-primary'}`} onClick={onConfirm}>
            {danger ? 'Delete' : 'Confirm'}
          </button>
        </div>
      </div>
    </div>
  );
}
