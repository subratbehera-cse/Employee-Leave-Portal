import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import DataTable from '../../components/DataTable';
import { getAllLeaveRequestsAdmin } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function LeaveRequestsPage() {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    getAllLeaveRequestsAdmin()
      .then((res) => setLeaves(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  const filtered = filter === 'ALL' ? leaves : leaves.filter((l) => l.status === filter);

  return (
    <Layout title="Leave Requests">
      <div className="page-header">
        <div>
          <h2>All Leave Requests</h2>
          <p>Company-wide view of every leave request ever submitted.</p>
        </div>
        <select value={filter} onChange={(e) => setFilter(e.target.value)} style={{ width: 180 }}>
          <option value="ALL">All</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Employee', 'Type', 'Dates', 'Days', 'Applied On', 'Status']}
            rows={filtered}
            emptyTitle="No leave requests found"
            renderRow={(r) => (
              <tr key={r.id}>
                <td>{r.userName} ({r.employeeCode})</td>
                <td>{r.leaveTypeName}</td>
                <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
                <td>{r.numberOfDays}</td>
                <td>{formatDate(r.appliedAt)}</td>
                <td><StatusBadge status={r.status} /></td>
              </tr>
            )}
          />
        </div>
      )}
    </Layout>
  );
}
