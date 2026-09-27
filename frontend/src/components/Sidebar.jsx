import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const LINKS = {
  EMPLOYEE: [
    { to: '/employee/dashboard', label: 'Dashboard', icon: '🏠' },
    { to: '/employee/apply-leave', label: 'Apply Leave', icon: '📝' },
    { to: '/employee/my-leaves', label: 'My Leaves', icon: '📋' },
    { to: '/employee/leave-balance', label: 'Leave Balance', icon: '📊' },
    { to: '/employee/profile', label: 'Profile', icon: '👤' },
  ],
  MANAGER: [
    { to: '/manager/dashboard', label: 'Dashboard', icon: '🏠' },
    { to: '/manager/team', label: 'Team', icon: '👥' },
    { to: '/manager/approvals', label: 'Leave Approvals', icon: '✅' },
  ],
  ADMIN: [
    { to: '/admin/dashboard', label: 'Dashboard', icon: '🏠' },
    { to: '/admin/employees', label: 'Employees', icon: '👥' },
    { to: '/admin/departments', label: 'Departments', icon: '🏢' },
    { to: '/admin/leave-types', label: 'Leave Types', icon: '🏷️' },
    { to: '/admin/leave-balances', label: 'Leave Balances', icon: '📊' },
    { to: '/admin/leave-requests', label: 'Leave Requests', icon: '📋' },
    { to: '/admin/audit-logs', label: 'Audit Logs', icon: '🧾' },
  ],
};

export default function Sidebar() {
  const { user } = useAuth();
  const links = LINKS[user?.role] || [];

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="dot" />
        Leave Portal
      </div>
      <nav>
        {links.map((link) => (
          <NavLink
            key={link.to}
            to={link.to}
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            <span>{link.icon}</span>
            <span>{link.label}</span>
          </NavLink>
        ))}
      </nav>
      <div className="sidebar-footer">Signed in as {user?.role?.toLowerCase()}</div>
    </aside>
  );
}
