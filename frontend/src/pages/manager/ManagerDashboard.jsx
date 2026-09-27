import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout';
import StatCard from '../../components/StatCard';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import DataTable from '../../components/DataTable';
import { getManagerDashboard } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function ManagerDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getManagerDashboard()
      .then((res) => setData(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="Manager Dashboard">
      <div className="page-header">
        <div>
          <h2>Team overview</h2>
          <p>Keep track of your team's leave activity and pending approvals.</p>
        </div>
        <Link className="btn btn-primary" to="/manager/approvals">Review Approvals</Link>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {data && (
        <>
          <div className="card-grid">
            <StatCard label="Team Size" value={data.teamSize} />
            <StatCard label="Pending Approvals" value={data.pendingApprovals} tone="accent" />
            <StatCard label="Approved" value={data.approvedRequests} tone="good" />
            <StatCard label="Rejected" value={data.rejectedRequests} tone="bad" />
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Recent Team Requests</h3></div>
            <DataTable
              columns={['Employee', 'Type', 'Dates', 'Days', 'Status']}
              rows={data.teamLeaveRequests}
              emptyTitle="No leave requests from your team yet"
              renderRow={(r) => (
                <tr key={r.id}>
                  <td>{r.userName}</td>
                  <td>{r.leaveTypeName}</td>
                  <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
                  <td>{r.numberOfDays}</td>
                  <td><StatusBadge status={r.status} /></td>
                </tr>
              )}
            />
          </div>
        </>
      )}
    </Layout>
  );
}
