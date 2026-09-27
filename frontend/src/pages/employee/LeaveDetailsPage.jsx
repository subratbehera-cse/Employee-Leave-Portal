import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import ConfirmDialog from '../../components/ConfirmDialog';
import { cancelLeave, getLeaveById } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate, formatDateTime } from '../../utils/formatters';

export default function LeaveDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [leave, setLeave] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showConfirm, setShowConfirm] = useState(false);
  const [cancelling, setCancelling] = useState(false);

  const load = () => {
    setLoading(true);
    getLeaveById(id)
      .then((res) => setLeave(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(load, [id]);

  const handleCancel = async () => {
    setCancelling(true);
    try {
      await cancelLeave(id);
      setShowConfirm(false);
      load();
    } catch (err) {
      setError(getErrorMessage(err));
      setShowConfirm(false);
    } finally {
      setCancelling(false);
    }
  };

  return (
    <Layout title="Leave Details">
      <div className="page-header">
        <div>
          <h2>Leave Request #{id}</h2>
          <p>Full details of this leave request.</p>
        </div>
        <button className="btn btn-outline" onClick={() => navigate(-1)}>Back</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {leave && (
        <div className="panel">
          <div className="panel-header">
            <h3>{leave.leaveTypeName}</h3>
            <StatusBadge status={leave.status} />
          </div>

          <div className="detail-grid">
            <div className="detail-item"><div className="label">Start Date</div><div className="val">{formatDate(leave.startDate)}</div></div>
            <div className="detail-item"><div className="label">End Date</div><div className="val">{formatDate(leave.endDate)}</div></div>
            <div className="detail-item"><div className="label">Number of Days</div><div className="val">{leave.numberOfDays}</div></div>
            <div className="detail-item"><div className="label">Applied On</div><div className="val">{formatDateTime(leave.appliedAt)}</div></div>
            {leave.reviewedAt && (
              <div className="detail-item"><div className="label">Reviewed On</div><div className="val">{formatDateTime(leave.reviewedAt)}</div></div>
            )}
            {leave.reviewedByName && (
              <div className="detail-item"><div className="label">Reviewed By</div><div className="val">{leave.reviewedByName}</div></div>
            )}
          </div>

          {leave.reason && (
            <div style={{ marginTop: 18 }}>
              <div className="detail-item"><div className="label">Reason</div><div className="val" style={{ fontWeight: 400 }}>{leave.reason}</div></div>
            </div>
          )}

          {leave.managerComment && (
            <div style={{ marginTop: 18 }}>
              <div className="detail-item"><div className="label">Manager Comment</div><div className="val" style={{ fontWeight: 400 }}>{leave.managerComment}</div></div>
            </div>
          )}

          {leave.status === 'PENDING' && (
            <div style={{ marginTop: 24 }}>
              <button className="btn btn-danger" onClick={() => setShowConfirm(true)}>Cancel this request</button>
            </div>
          )}
        </div>
      )}

      {showConfirm && (
        <ConfirmDialog
          title="Cancel leave request"
          message="Are you sure you want to cancel this leave request? This action cannot be undone."
          confirmLabel="Yes, cancel it"
          danger
          loading={cancelling}
          onConfirm={handleCancel}
          onCancel={() => setShowConfirm(false)}
        />
      )}
    </Layout>
  );
}
