import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import ConfirmDialog from '../../components/ConfirmDialog';
import { createDepartment, deleteDepartment, getDepartments, updateDepartment } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

export default function DepartmentsPage() {
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = () => {
    setLoading(true);
    getDepartments()
      .then((res) => setDepartments(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => { setEditingId(null); setForm({ name: '', description: '' }); setModalOpen(true); };
  const openEdit = (d) => { setEditingId(d.id); setForm({ name: d.name, description: d.description || '' }); setModalOpen(true); };
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (editingId) await updateDepartment(editingId, form);
      else await createDepartment(form);
      setModalOpen(false);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await deleteDepartment(deleteTarget.id);
      setDeleteTarget(null);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
      setDeleteTarget(null);
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Layout title="Departments">
      <div className="page-header">
        <div>
          <h2>Departments</h2>
          <p>Organize employees into departments.</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>Add Department</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Name', 'Description', 'Employees', '']}
            rows={departments}
            emptyTitle="No departments yet"
            renderRow={(d) => (
              <tr key={d.id}>
                <td>{d.name}</td>
                <td>{d.description || '—'}</td>
                <td>{d.employeeCount}</td>
                <td style={{ display: 'flex', gap: 8 }}>
                  <button className="btn btn-outline btn-sm" onClick={() => openEdit(d)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => setDeleteTarget(d)}>Delete</button>
                </td>
              </tr>
            )}
          />
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editingId ? 'Edit department' : 'Add department'}
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
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete department"
          message={`Delete "${deleteTarget.name}"? This is only possible if no employees are assigned to it.`}
          confirmLabel="Delete"
          danger
          loading={deleting}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
    </Layout>
  );
}
