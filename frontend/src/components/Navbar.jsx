import { useAuth } from '../context/AuthContext';

const initials = (user) => `${user?.firstName?.[0] || ''}${user?.lastName?.[0] || ''}`.toUpperCase();

export default function Navbar({ title }) {
  const { user, logout } = useAuth();

  return (
    <header className="navbar">
      <div className="navbar-title">{title}</div>
      <div className="navbar-user">
        <div className="navbar-avatar">{initials(user)}</div>
        <div>
          <div style={{ fontWeight: 600, color: '#16241f' }}>{user?.firstName} {user?.lastName}</div>
          <div>{user?.role}</div>
        </div>
        <button className="logout-btn" onClick={logout}>Log out</button>
      </div>
    </header>
  );
}
