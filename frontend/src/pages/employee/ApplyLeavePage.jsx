import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Layout from '../../components/Layout';
import ErrorAlert from '../../components/ErrorAlert';
import { applyLeave, getMyBalances } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

export default function ApplyLeavePage() {
  const navigate = useNavigate();
  const [balances, setBalances] = useState([]);
  const [form, setForm] = useState({ leaveTypeId: '', startDate: '', endDate: '', reason: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    getMyBalances()
      .then((res) => {
        setBalances(res.data);
        if (res.data.length > 0) {
          setForm((f) => ({ ...f, leaveTypeId: String(res.data[0].leaveTypeId) }));
        }
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const selectedBalance = balances.find((b) => String(b.leaveTypeId) === form.leaveTypeId);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSubmitting(true);
    try {
      await applyLeave({ ...form, leaveTypeId: Number(form.leaveTypeId) });
      setSuccess('Leave request submitted successfully.');
      setTimeout(() => navigate('/employee/my-leaves'), 900);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Layout title="Apply for Leave">
      <div className="page-header">
        <div>
          <h2>Apply for Leave</h2>
          <p>Submit a new leave request for approval.</p>
        </div>
      </div>

      <div className="panel" style={{ maxWidth: 560 }}>
        <ErrorAlert message={error} />
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="leaveTypeId">Leave type</label>
            <select id="leaveTypeId" name="leaveTypeId" required value={form.leaveTypeId} onChange={handleChange}>
              <option value="" disabled>Select a leave type</option>
              {balances.map((b) => (
                <option key={b.leaveTypeId} value={b.leaveTypeId}>{b.leaveTypeName}</option>
              ))}
            </select>
            {selectedBalance && (
              <span style={{ fontSize: '0.8rem', color: '#6f7a72' }}>
                {selectedBalance.remainingDays} of {selectedBalance.totalDays} days remaining
              </span>
            )}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="startDate">Start date</label>
              <input id="startDate" name="startDate" type="date" required value={form.startDate} onChange={handleChange} />
            </div>
            <div className="form-group">
              <label htmlFor="endDate">End date</label>
              <input id="endDate" name="endDate" type="date" required value={form.endDate} onChange={handleChange} />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="reason">Reason</label>
            <textarea id="reason" name="reason" placeholder="Briefly describe the reason for leave" value={form.reason} onChange={handleChange} />
          </div>

          <button className="btn btn-primary" type="submit" disabled={submitting || balances.length === 0}>
            {submitting ? 'Submitting…' : 'Submit request'}
          </button>
        </form>
      </div>
    </Layout>
  );
}
