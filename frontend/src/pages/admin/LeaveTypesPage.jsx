import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import ConfirmDialog from '../../components/ConfirmDialog';
import { createLeaveType, deleteLeaveType, getLeaveTypes, updateLeaveType } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

const emptyForm = { name: '', description: '', defaultDays: 10, isActive: true };

export default function LeaveTypesPage() {
  const [leaveTypes, setLeaveTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [deactivateTarget, setDeactivateTarget] = useState(null);
  const [deactivating, setDeactivating] = useState(false);

  const load = () => {
    setLoading(true);
    getLeaveTypes()
      .then((res) => setLeaveTypes(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => { setEditingId(null); setForm(emptyForm); setModalOpen(true); };
  const openEdit = (lt) => {
    setEditingId(lt.id);
    setForm({ name: lt.name, description: lt.description || '', defaultDays: lt.defaultDays, isActive: lt.isActive });
    setModalOpen(true);
  };
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const payload = { ...form, defaultDays: Number(form.defaultDays) };
      if (editingId) await updateLeaveType(editingId, payload);
      else await createLeaveType(payload);
      setModalOpen(false);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleDeactivate = async () => {
    setDeactivating(true);
    try {
      await deleteLeaveType(deactivateTarget.id);
      setDeactivateTarget(null);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
      setDeactivateTarget(null);
    } finally {
      setDeactivating(false);
    }
  };

  return (
    <Layout title="Leave Types">
      <div className="page-header">
        <div>
          <h2>Leave Types</h2>
          <p>Define the kinds of leave employees can request and their default allocation.</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>Add Leave Type</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Name', 'Description', 'Default Days', 'Status', '']}
            rows={leaveTypes}
            emptyTitle="No leave types yet"
            renderRow={(lt) => (
              <tr key={lt.id}>
                <td>{lt.name}</td>
                <td>{lt.description || '—'}</td>
                <td>{lt.defaultDays}</td>
                <td>{lt.isActive ? 'Active' : 'Inactive'}</td>
                <td style={{ display: 'flex', gap: 8 }}>
                  <button className="btn btn-outline btn-sm" onClick={() => openEdit(lt)}>Edit</button>
                  {lt.isActive && (
                    <button className="btn btn-danger btn-sm" onClick={() => setDeactivateTarget(lt)}>Deactivate</button>
                  )}
                </td>
              </tr>
            )}
          />
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editingId ? 'Edit leave type' : 'Add leave type'}
          onClose={() => setModalOpen(false)}
          actions={
            <>
              <button className="btn btn-outline" onClick={() => setModalOpen(false)} disabled={saving}>Cancel</button>
              <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
            </>
          }
        >
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Name</label>
              <input name="name" required value={form.name} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea name="description" value={form.description} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label>Default days per year</label>
              <input name="defaultDays" type="number" min="0" required value={form.defaultDays} onChange={handleChange} />
            </div>
            <div className="form-group" style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
              <input id="isActive" name="isActive" type="checkbox" checked={form.isActive} onChange={handleChange} style={{ width: 'auto' }} />
              <label htmlFor="isActive" style={{ fontWeight: 500 }}>Active</label>
            </div>
          </form>
        </Modal>
      )}

      {deactivateTarget && (
        <ConfirmDialog
          title="Deactivate leave type"
          message={`Deactivate "${deactivateTarget.name}"? Employees will no longer be able to apply using this leave type.`}
          confirmLabel="Deactivate"
          danger
          loading={deactivating}
          onConfirm={handleDeactivate}
          onCancel={() => setDeactivateTarget(null)}
        />
      )}
    </Layout>
  );
}
