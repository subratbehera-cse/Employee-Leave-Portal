import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import ConfirmDialog from '../../components/ConfirmDialog';
import {
  createEmployee, deleteEmployee, getAllEmployees, getDepartments, updateEmployee,
} from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

const emptyForm = {
  firstName: '', lastName: '', email: '', password: '', phone: '',
  role: 'EMPLOYEE', departmentId: '', managerId: '', joiningDate: '', status: 'ACTIVE',
};

export default function EmployeesPage() {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
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
    Promise.all([getAllEmployees(), getDepartments()])
      .then(([empRes, deptRes]) => {
        setEmployees(empRes.data);
        setDepartments(deptRes.data);
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openCreate = () => {
    setEditingId(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  const openEdit = (emp) => {
    setEditingId(emp.id);
    setForm({
      firstName: emp.firstName, lastName: emp.lastName, email: emp.email, password: '',
      phone: emp.phone || '', role: emp.role, departmentId: emp.departmentId || '',
      managerId: emp.managerId || '', joiningDate: emp.joiningDate || '', status: emp.status,
    });
    setModalOpen(true);
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const managers = employees.filter((e) => e.role === 'MANAGER' || e.role === 'ADMIN');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      const payload = {
        ...form,
        departmentId: form.departmentId ? Number(form.departmentId) : null,
        managerId: form.managerId ? Number(form.managerId) : null,
      };
      if (editingId) {
        delete payload.password;
        await updateEmployee(editingId, payload);
      } else {
        await createEmployee(payload);
      }
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
      await deleteEmployee(deleteTarget.id);
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
    <Layout title="Employees">
      <div className="page-header">
        <div>
          <h2>Employees</h2>
          <p>Manage every employee, their role, department and reporting manager.</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>Add Employee</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Code', 'Name', 'Email', 'Role', 'Department', 'Manager', 'Status', '']}
            rows={employees}
            emptyTitle="No employees yet"
            renderRow={(e) => (
              <tr key={e.id}>
                <td>{e.employeeCode}</td>
                <td>{e.firstName} {e.lastName}</td>
                <td>{e.email}</td>
                <td>{e.role}</td>
                <td>{e.departmentName || '—'}</td>
                <td>{e.managerName || '—'}</td>
                <td>{e.status}</td>
                <td style={{ display: 'flex', gap: 8 }}>
                  <button className="btn btn-outline btn-sm" onClick={() => openEdit(e)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => setDeleteTarget(e)}>Delete</button>
                </td>
              </tr>
            )}
          />
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editingId ? 'Edit employee' : 'Add employee'}
          onClose={() => setModalOpen(false)}
          actions={
            <>
              <button className="btn btn-outline" onClick={() => setModalOpen(false)} disabled={saving}>Cancel</button>
              <button className="btn btn-primary" onClick={handleSubmit} disabled={saving}>{saving ? 'Saving…' : 'Save'}</button>
            </>
          }
        >
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>First name</label>
                <input name="firstName" required value={form.firstName} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Last name</label>
                <input name="lastName" required value={form.lastName} onChange={handleChange} />
              </div>
            </div>
            <div className="form-group">
              <label>Email</label>
              <input name="email" type="email" required value={form.email} onChange={handleChange} />
            </div>
            {!editingId && (
              <div className="form-group">
                <label>Password</label>
                <input name="password" type="password" required minLength={6} value={form.password} onChange={handleChange} />
              </div>
            )}
            <div className="form-row">
              <div className="form-group">
                <label>Phone</label>
                <input name="phone" value={form.phone} onChange={handleChange} />
              </div>
              <div className="form-group">
                <label>Role</label>
                <select name="role" value={form.role} onChange={handleChange}>
                  <option value="EMPLOYEE">Employee</option>
                  <option value="MANAGER">Manager</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Department</label>
                <select name="departmentId" value={form.departmentId} onChange={handleChange}>
                  <option value="">None</option>
                  {departments.map((d) => <option key={d.id} value={d.id}>{d.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Manager</label>
                <select name="managerId" value={form.managerId} onChange={handleChange}>
                  <option value="">None</option>
                  {managers.filter((m) => m.id !== editingId).map((m) => (
                    <option key={m.id} value={m.id}>{m.firstName} {m.lastName}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Joining date</label>
                <input name="joiningDate" type="date" value={form.joiningDate || ''} onChange={handleChange} />
              </div>
              {editingId && (
                <div className="form-group">
                  <label>Status</label>
                  <select name="status" value={form.status} onChange={handleChange}>
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                  </select>
                </div>
              )}
            </div>
          </form>
        </Modal>
      )}

      {deleteTarget && (
        <ConfirmDialog
          title="Delete employee"
          message={`Delete ${deleteTarget.firstName} ${deleteTarget.lastName}? Employees with existing leave history will be deactivated instead of permanently removed.`}
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
