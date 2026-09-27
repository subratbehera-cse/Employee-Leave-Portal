import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout';
import StatCard from '../../components/StatCard';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import EmptyState from '../../components/EmptyState';
import { getEmployeeDashboard } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function EmployeeDashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getEmployeeDashboard()
      .then((res) => setData(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="Dashboard">
      <div className="page-header">
        <div>
          <h2>Your overview</h2>
          <p>A snapshot of your leave balance and recent activity.</p>
        </div>
        <Link className="btn btn-primary" to="/employee/apply-leave">Apply for Leave</Link>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {data && (
        <>
          <div className="card-grid">
            <StatCard label="Pending Requests" value={data.pendingRequests} tone="accent" />
            <StatCard label="Approved Leaves" value={data.approvedRequests} tone="good" />
            <StatCard label="Rejected Leaves" value={data.rejectedRequests} tone="bad" />
            <StatCard label="Upcoming Leave" value={data.upcomingLeave ? formatDate(data.upcomingLeave.startDate) : '—'} />
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Leave Balance</h3></div>
            {data.leaveBalances.length === 0 ? (
              <EmptyState title="No leave balance configured" description="Contact an administrator to set up your leave balances." />
            ) : (
              <div className="card-grid">
                {data.leaveBalances.map((b) => (
                  <div className="stat-card" key={b.id}>
                    <div className="label">{b.leaveTypeName}</div>
                    <div className="value">{b.remainingDays} <span style={{ fontSize: '0.9rem', color: '#6f7a72' }}>/ {b.totalDays} days</span></div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Recent Requests</h3></div>
            {data.recentRequests.length === 0 ? (
              <EmptyState title="No leave requests yet" description="Your applications will show up here." />
            ) : (
              <table className="data-table">
                <thead>
                  <tr><th>Type</th><th>Dates</th><th>Days</th><th>Status</th></tr>
                </thead>
                <tbody>
                  {data.recentRequests.map((r) => (
                    <tr key={r.id}>
                      <td>{r.leaveTypeName}</td>
                      <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
                      <td>{r.numberOfDays}</td>
                      <td><StatusBadge status={r.status} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}
    </Layout>
  );
}
