import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { approveLeave, getTeamLeaves, rejectLeave } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function ApprovalsPage() {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reviewing, setReviewing] = useState(null); // { leave, action }
  const [comment, setComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [filter, setFilter] = useState('PENDING');

  const load = () => {
    setLoading(true);
    getTeamLeaves()
      .then((res) => setLeaves(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, []);

  const openReview = (leave, action) => {
    setReviewing({ leave, action });
    setComment('');
  };

  const submitReview = async () => {
    setSubmitting(true);
    setError('');
    try {
      const action = reviewing.action === 'approve' ? approveLeave : rejectLeave;
      await action(reviewing.leave.id, { comment });
      setReviewing(null);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = filter === 'ALL' ? leaves : leaves.filter((l) => l.status === filter);

  return (
    <Layout title="Leave Approvals">
      <div className="page-header">
        <div>
          <h2>Leave Approvals</h2>
          <p>Review and act on leave requests from your team.</p>
        </div>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} style={{ width: 180 }}>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
          <option value="CANCELLED">Cancelled</option>
          <option value="ALL">All</option>
        </select>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Employee', 'Type', 'Dates', 'Days', 'Status', '']}
            rows={filtered}
            emptyTitle="Nothing to review"
            emptyDescription="Requests matching this filter will appear here."
            renderRow={(r) => (
              <tr key={r.id}>
                <td>{r.userName}</td>
                <td>{r.leaveTypeName}</td>
                <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
                <td>{r.numberOfDays}</td>
                <td><StatusBadge status={r.status} /></td>
                <td style={{ display: 'flex', gap: 8 }}>
                  {r.status === 'PENDING' ? (
                    <>
                      <button className="btn btn-primary btn-sm" onClick={() => openReview(r, 'approve')}>Approve</button>
                      <button className="btn btn-danger btn-sm" onClick={() => openReview(r, 'reject')}>Reject</button>
                    </>
                  ) : (
                    <Link className="btn btn-outline btn-sm" to={`/manager/leaves/${r.id}`}>View</Link>
                  )}
                </td>
              </tr>
            )}
          />
        </div>
      )}

      {reviewing && (
        <Modal
          title={reviewing.action === 'approve' ? 'Approve leave request' : 'Reject leave request'}
          onClose={() => setReviewing(null)}
          actions={
            <>
              <button className="btn btn-outline" onClick={() => setReviewing(null)} disabled={submitting}>Cancel</button>
              <button
                className={reviewing.action === 'approve' ? 'btn btn-primary' : 'btn btn-danger'}
                onClick={submitReview}
                disabled={submitting}
              >
                {submitting ? 'Submitting…' : reviewing.action === 'approve' ? 'Approve' : 'Reject'}
              </button>
            </>
          }
        >
          <p style={{ color: '#6f7a72', fontSize: '0.9rem', marginBottom: 12 }}>
            {reviewing.leave.userName} — {reviewing.leave.leaveTypeName}, {formatDate(reviewing.leave.startDate)} to {formatDate(reviewing.leave.endDate)}
          </p>
          <div className="form-group">
            <label htmlFor="comment">Remarks (optional)</label>
            <textarea id="comment" value={comment} onChange={(e) => setComment(e.target.value)} placeholder="Add a note for the employee" />
          </div>
        </Modal>
      )}
    </Layout>
  );
}
