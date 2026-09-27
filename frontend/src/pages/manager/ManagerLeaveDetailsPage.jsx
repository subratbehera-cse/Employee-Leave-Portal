import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import { getLeaveById } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate, formatDateTime } from '../../utils/formatters';

export default function ManagerLeaveDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [leave, setLeave] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getLeaveById(id)
      .then((res) => setLeave(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [id]);

  return (
    <Layout title="Leave Details">
      <div className="page-header">
        <div>
          <h2>Leave Request #{id}</h2>
          <p>Details of this team member's leave request.</p>
        </div>
        <button className="btn btn-outline" onClick={() => navigate(-1)}>Back</button>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {leave && (
        <div className="panel">
          <div className="panel-header">
            <h3>{leave.userName} — {leave.leaveTypeName}</h3>
            <StatusBadge status={leave.status} />
          </div>
          <div className="detail-grid">
            <div className="detail-item"><div className="label">Employee Code</div><div className="val">{leave.employeeCode}</div></div>
            <div className="detail-item"><div className="label">Start Date</div><div className="val">{formatDate(leave.startDate)}</div></div>
            <div className="detail-item"><div className="label">End Date</div><div className="val">{formatDate(leave.endDate)}</div></div>
            <div className="detail-item"><div className="label">Days</div><div className="val">{leave.numberOfDays}</div></div>
            <div className="detail-item"><div className="label">Applied On</div><div className="val">{formatDateTime(leave.appliedAt)}</div></div>
            {leave.reviewedAt && <div className="detail-item"><div className="label">Reviewed On</div><div className="val">{formatDateTime(leave.reviewedAt)}</div></div>}
          </div>
          {leave.reason && <div style={{ marginTop: 18 }}><div className="detail-item"><div className="label">Reason</div><div className="val" style={{ fontWeight: 400 }}>{leave.reason}</div></div></div>}
          {leave.managerComment && <div style={{ marginTop: 18 }}><div className="detail-item"><div className="label">Your Remarks</div><div className="val" style={{ fontWeight: 400 }}>{leave.managerComment}</div></div></div>}
        </div>
      )}
    </Layout>
  );
}
