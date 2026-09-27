import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import StatCard from '../../components/StatCard';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import DataTable from '../../components/DataTable';
import { getAdminDashboard } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function AdminDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getAdminDashboard()
      .then((res) => setData(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="Admin Dashboard">
      <div className="page-header">
        <div>
          <h2>Organization overview</h2>
          <p>Company-wide employee and leave statistics.</p>
        </div>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {data && (
        <>
          <div className="card-grid">
            <StatCard label="Total Employees" value={data.totalEmployees} />
            <StatCard label="Departments" value={data.totalDepartments} />
            <StatCard label="Pending Requests" value={data.pendingRequests} tone="accent" />
            <StatCard label="Approved" value={data.approvedRequests} tone="good" />
            <StatCard label="Rejected" value={data.rejectedRequests} tone="bad" />
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Leave Type Distribution</h3></div>
            {Object.keys(data.leaveTypeDistribution || {}).length === 0 ? (
              <p style={{ color: '#6f7a72', fontSize: '0.9rem' }}>No leave requests recorded yet.</p>
            ) : (
              <div className="card-grid">
                {Object.entries(data.leaveTypeDistribution).map(([name, count]) => (
                  <div className="stat-card" key={name}>
                    <div className="label">{name}</div>
                    <div className="value">{count}</div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Recent Leave Requests</h3></div>
            <DataTable
              columns={['Employee', 'Type', 'Dates', 'Status']}
              rows={data.recentRequests}
              emptyTitle="No leave requests yet"
              renderRow={(r) => (
                <tr key={r.id}>
                  <td>{r.userName}</td>
                  <td>{r.leaveTypeName}</td>
                  <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
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
