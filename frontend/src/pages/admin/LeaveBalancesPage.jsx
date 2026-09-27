import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import ConfirmDialog from '../../components/ConfirmDialog';
import {
  createLeaveBalance, deleteLeaveBalance, getAllEmployees, getAllLeaveBalances, getLeaveTypes, updateLeaveBalance,
} from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

const currentYear = new Date().getFullYear();
const emptyForm = { userId: '', leaveTypeId: '', totalDays: 10, year: currentYear };

export default function LeaveBalancesPage() {
  const [balances, setBalances] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [leaveTypes, setLeaveTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = () => {
    setLoading(true);
    Promise.all([getAllLeaveBalances(), getAllEmployees(), getLeaveTypes()])
      .then(([balRes, empRes, typeRes]) => {
        setBalances(balRes.data);
        setEmployees(empRes.data);
        setLeaveTypes(typeRes.data);
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => { setEditingId(null); setForm(emptyForm); setModalOpen(true); };
  const openEdit = (b) => {
    setEditingId(b.id);
    setForm({ userId: b.userId, leaveTypeId: b.leaveTypeId, totalDays: b.totalDays, year: b.year });
    setModalOpen(true);
  };
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const payload = {
        userId: Number(form.userId), leaveTypeId: Number(form.leaveTypeId),
        totalDays: Number(form.totalDays), year: Number(form.year),
      };
      if (editingId) await updateLeaveBalance(editingId, payload);
      else await createLeaveBalance(payload);
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
      await deleteLeaveBalance(deleteTarget.id);
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
    <Layout title="Leave Balances">
      <div className="page-header">
        <div>
          <h2>Leave Balances</h2>
          <p>Allocate and adjust yearly leave balances per employee.</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>Add Balance</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Employee', 'Leave Type', 'Year', 'Total', 'Used', 'Remaining', '']}
            rows={balances}
            emptyTitle="No leave balances configured yet"
            renderRow={(b) => (
              <tr key={b.id}>
                <td>{b.userName}</td>
                <td>{b.leaveTypeName}</td>
                <td>{b.year}</td>
                <td>{b.totalDays}</td>
                <td>{b.usedDays}</td>
                <td>{b.remainingDays}</td>
                <td style={{ display: 'flex', gap: 8 }}>
                  <button className="btn btn-outline btn-sm" onClick={() => openEdit(b)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => setDeleteTarget(b)}>Delete</button>
                </td>
              </tr>
            )}
          />
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editingId ? 'Edit leave balance' : 'Add leave balance'}
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
              <label>Employee</label>
              <select name="userId" required disabled={!!editingId} value={form.userId} onChange={handleChange}>
                <option value="" disabled>Select employee</option>
                {employees.map((e) => <option key={e.id} value={e.id}>{e.firstName} {e.lastName} ({e.employeeCode})</option>)}
              </select>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Leave Type</label>
                <select name="leaveTypeId" required disabled={!!editingId} value={form.leaveTypeId} onChange={handleChange}>
                  <option value="" disabled>Select type</option>
                  {leaveTypes.map((lt) => <option key={lt.id} value={lt.id}>{lt.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Year</label>
                <input name="year" type="number" required disabled={!!editingId} value={form.year} onChange={handleChange} />
              </div>
            </div>
            <div className="form-group">
              <label>Total days</label>
              <input name="totalDays" type="number" min="0" step="0.5" required value={form.totalDays} onChange={handleChange} />
            </div>
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete leave balance"
          message={`Delete the ${deleteTarget.leaveTypeName} balance for ${deleteTarget.userName}?`}
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
