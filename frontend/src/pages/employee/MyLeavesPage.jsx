import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import StatusBadge from '../../components/StatusBadge';
import DataTable from '../../components/DataTable';
import { getMyLeaves } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function MyLeavesPage() {
  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getMyLeaves()
      .then((res) => setLeaves(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="My Leaves">
      <div className="page-header">
        <div>
          <h2>My Leave Requests</h2>
          <p>Track the status of every leave request you have submitted.</p>
        </div>
        <Link className="btn btn-primary" to="/employee/apply-leave">Apply for Leave</Link>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Type', 'Dates', 'Days', 'Applied On', 'Status', '']}
            rows={leaves}
            emptyTitle="No leave requests yet"
            emptyDescription="Applications you submit will appear here."
            renderRow={(r) => (
              <tr key={r.id}>
                <td>{r.leaveTypeName}</td>
                <td>{formatDate(r.startDate)} – {formatDate(r.endDate)}</td>
                <td>{r.numberOfDays}</td>
                <td>{formatDate(r.appliedAt)}</td>
                <td><StatusBadge status={r.status} /></td>
                <td><Link className="btn btn-outline btn-sm" to={`/employee/leaves/${r.id}`}>View</Link></td>
              </tr>
            )}
          />
        </div>
      )}
    </Layout>
  );
}
