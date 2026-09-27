import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import EmptyState from '../../components/EmptyState';
import { getMyBalances } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';

export default function LeaveBalancePage() {
  const [balances, setBalances] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getMyBalances()
      .then((res) => setBalances(res.data))
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  return (
    <Layout title="Leave Balance">
      <div className="page-header">
        <div>
          <h2>Leave Balance</h2>
          <p>Your allocated, used and remaining leave for this year.</p>
        </div>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />

      {!loading && balances.length === 0 && (
        <EmptyState title="No leave balance configured" description="Contact an administrator to set up your leave balances." />
      )}

      {!loading && balances.length > 0 && (
        <div className="card-grid">
          {balances.map((b) => {
            const pctUsed = b.totalDays > 0 ? Math.min(100, Math.round((b.usedDays / b.totalDays) * 100)) : 0;
            return (
              <div className="panel" key={b.id} style={{ marginBottom: 0 }}>
                <div className="panel-header"><h3>{b.leaveTypeName}</h3></div>
                <div style={{ fontSize: '2rem', fontWeight: 700, fontFamily: "'Space Grotesk', sans-serif" }}>
                  {b.remainingDays} <span style={{ fontSize: '1rem', color: '#6f7a72', fontWeight: 500 }}>days left</span>
                </div>
                <div style={{ height: 8, background: '#ececdf', borderRadius: 4, marginTop: 14, overflow: 'hidden' }}>
                  <div style={{ height: '100%', width: `${pctUsed}%`, background: '#2f5d50' }} />
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 8, fontSize: '0.8rem', color: '#6f7a72' }}>
                  <span>{b.usedDays} used</span>
                  <span>{b.totalDays} total</span>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </Layout>
  );
}
