import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import DataTable from '../../components/DataTable';
import { getAuditLogs } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { formatDateTime } from '../../utils/formatters';

export default function AuditLogsPage() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    getAuditLogs(page, 20)
      .then((res) => {
        setLogs(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <Layout title="Audit Logs">
      <div className="page-header">
        <div>
          <h2>Audit Logs</h2>
          <p>A traceable record of key actions across the system.</p>
        </div>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && (
        <div className="panel">
          <DataTable
            columns={['When', 'User', 'Action', 'Entity', 'Details']}
            rows={logs}
            emptyTitle="No audit log entries yet"
            renderRow={(log) => (
              <tr key={log.id}>
                <td>{formatDateTime(log.createdAt)}</td>
                <td>{log.userName}</td>
                <td>{log.action}</td>
                <td>{log.entityType}{log.entityId ? ` #${log.entityId}` : ''}</td>
                <td>{log.description}</td>
              </tr>
            )}
          />

          {totalPages > 1 && (
            <div style={{ display: 'flex', justifyContent: 'center', gap: 10, marginTop: 18 }}>
              <button className="btn btn-outline btn-sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Previous</button>
              <span style={{ alignSelf: 'center', fontSize: '0.85rem', color: '#6f7a72' }}>Page {page + 1} of {totalPages}</span>
              <button className="btn btn-outline btn-sm" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next</button>
            </div>
          )}
        </div>
      )}
    </Layout>
  );
}
