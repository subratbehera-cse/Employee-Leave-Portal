import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import { getTeamMembers } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDate } from '../../utils/formatters';

export default function TeamPage() {
  const [team, setTeam] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getTeamMembers()
      .then((res) => setTeam(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="My Team">
      <div className="page-header">
        <div>
          <h2>My Team</h2>
          <p>Everyone who reports directly to you.</p>
        </div>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['Employee Code', 'Name', 'Email', 'Department', 'Joined']}
            rows={team}
            emptyTitle="No team members yet"
            emptyDescription="Employees assigned to you as their manager will appear here."
            renderRow={(m) => (
              <tr key={m.id}>
                <td>{m.employeeCode}</td>
                <td>{m.firstName} {m.lastName}</td>
                <td>{m.email}</td>
                <td>{m.departmentName || '—'}</td>
                <td>{formatDate(m.joiningDate)}</td>
              </tr>
            )}
          />
        </div>
      )}
    </Layout>
  );
}
