import { useEffect, useState } from 'react';
import Layout from '../../components/Layout';
import Loading from '../../components/Loading';
import ErrorAlert from '../../components/ErrorAlert';
import { getMyProfile, updateMyProfile } from '../../api/endpoints';
import { getErrorMessage } from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import { formatDate } from '../../utils/formatters';

export default function ProfilePage() {
  const { updateStoredUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ firstName: '', lastName: '', phone: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    getMyProfile()
      .then((res) => {
        setProfile(res.data);
        setForm({ firstName: res.data.firstName, lastName: res.data.lastName, phone: res.data.phone || '' });
      })
      .catch((err) => setError(getErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSaving(true);
    try {
      const { data } = await updateMyProfile(form);
      setProfile(data);
      updateStoredUser(data);
      setSuccess('Profile updated successfully.');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Layout title="My Profile">
      <div className="page-header">
        <div>
          <h2>My Profile</h2>
          <p>View and update your personal details.</p>
        </div>
      </div>

      {loading && <Loading />}
      <ErrorAlert message={error} />
      {success && <div className="alert alert-success">{success}</div>}

      {profile && (
        <>
          <div className="panel">
            <div className="panel-header"><h3>Employment details</h3></div>
            <div className="detail-grid">
              <div className="detail-item"><div className="label">Employee Code</div><div className="val">{profile.employeeCode}</div></div>
              <div className="detail-item"><div className="label">Email</div><div className="val">{profile.email}</div></div>
              <div className="detail-item"><div className="label">Department</div><div className="val">{profile.departmentName || '—'}</div></div>
              <div className="detail-item"><div className="label">Manager</div><div className="val">{profile.managerName || '—'}</div></div>
              <div className="detail-item"><div className="label">Joining Date</div><div className="val">{formatDate(profile.joiningDate)}</div></div>
              <div className="detail-item"><div className="label">Status</div><div className="val">{profile.status}</div></div>
            </div>
          </div>

          <div className="panel">
            <div className="panel-header"><h3>Edit details</h3></div>
            <form onSubmit={handleSubmit} style={{ maxWidth: 480 }}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="firstName">First name</label>
                  <input id="firstName" name="firstName" required value={form.firstName} onChange={handleChange} />
                </div>
                <div className="form-group">
                  <label htmlFor="lastName">Last name</label>
                  <input id="lastName" name="lastName" required value={form.lastName} onChange={handleChange} />
                </div>
              </div>
              <div className="form-group">
                <label htmlFor="phone">Phone</label>
                <input id="phone" name="phone" value={form.phone} onChange={handleChange} />
              </div>
              <button className="btn btn-primary" type="submit" disabled={saving}>
                {saving ? 'Saving…' : 'Save changes'}
              </button>
            </form>
          </div>
        </>
      )}
    </Layout>
  );
}
