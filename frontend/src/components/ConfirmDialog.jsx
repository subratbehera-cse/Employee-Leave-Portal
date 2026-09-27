import Modal from './Modal';

export default function ConfirmDialog({ title = 'Are you sure?', message, confirmLabel = 'Confirm', danger, onConfirm, onCancel, loading }) {
  return (
    <Modal
      title={title}
      onClose={onCancel}
      actions={
        <>
          <button className="btn btn-outline" onClick={onCancel} disabled={loading}>Cancel</button>
          <button className={danger ? 'btn btn-danger' : 'btn btn-primary'} onClick={onConfirm} disabled={loading}>
            {loading ? 'Please wait…' : confirmLabel}
          </button>
        </>
      }
    >
      <p style={{ color: '#6f7a72', fontSize: '0.9rem' }}>{message}</p>
    </Modal>
  );
}
