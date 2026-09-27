import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

import EmployeeDashboard from './pages/employee/EmployeeDashboard';
import ProfilePage from './pages/employee/ProfilePage';
import ApplyLeavePage from './pages/employee/ApplyLeavePage';
import MyLeavesPage from './pages/employee/MyLeavesPage';
import LeaveDetailsPage from './pages/employee/LeaveDetailsPage';
import LeaveBalancePage from './pages/employee/LeaveBalancePage';

import ManagerDashboard from './pages/manager/ManagerDashboard';
import TeamPage from './pages/manager/TeamPage';
import ApprovalsPage from './pages/manager/ApprovalsPage';
import ManagerLeaveDetailsPage from './pages/manager/ManagerLeaveDetailsPage';

import AdminDashboard from './pages/admin/AdminDashboard';
import EmployeesPage from './pages/admin/EmployeesPage';
import DepartmentsPage from './pages/admin/DepartmentsPage';
import LeaveTypesPage from './pages/admin/LeaveTypesPage';
import LeaveBalancesPage from './pages/admin/LeaveBalancesPage';
import LeaveRequestsPage from './pages/admin/LeaveRequestsPage';
import AuditLogsPage from './pages/admin/AuditLogsPage';

function RootRedirect() {
  const { user, token } = useAuth();
  if (!token || !user) return <Navigate to="/login" replace />;
  const home = { EMPLOYEE: '/employee/dashboard', MANAGER: '/manager/dashboard', ADMIN: '/admin/dashboard' };
  return <Navigate to={home[user.role] || '/login'} replace />;
}

function UnauthorizedPage() {
  return (
    <div className="auth-wrap">
      <div className="auth-card" style={{ textAlign: 'center' }}>
        <h1>403</h1>
        <p className="auth-sub">You don&apos;t have permission to view this page.</p>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<RootRedirect />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* Employee */}
          <Route path="/employee/dashboard" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><EmployeeDashboard /></ProtectedRoute>} />
          <Route path="/employee/profile" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><ProfilePage /></ProtectedRoute>} />
          <Route path="/employee/apply-leave" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><ApplyLeavePage /></ProtectedRoute>} />
          <Route path="/employee/my-leaves" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><MyLeavesPage /></ProtectedRoute>} />
          <Route path="/employee/leave-balance" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><LeaveBalancePage /></ProtectedRoute>} />
          <Route path="/employee/leaves/:id" element={<ProtectedRoute allowedRoles={['EMPLOYEE']}><LeaveDetailsPage /></ProtectedRoute>} />

          {/* Manager */}
          <Route path="/manager/dashboard" element={<ProtectedRoute allowedRoles={['MANAGER']}><ManagerDashboard /></ProtectedRoute>} />
          <Route path="/manager/team" element={<ProtectedRoute allowedRoles={['MANAGER']}><TeamPage /></ProtectedRoute>} />
          <Route path="/manager/approvals" element={<ProtectedRoute allowedRoles={['MANAGER']}><ApprovalsPage /></ProtectedRoute>} />
          <Route path="/manager/leaves/:id" element={<ProtectedRoute allowedRoles={['MANAGER']}><ManagerLeaveDetailsPage /></ProtectedRoute>} />

          {/* Admin */}
          <Route path="/admin/dashboard" element={<ProtectedRoute allowedRoles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/employees" element={<ProtectedRoute allowedRoles={['ADMIN']}><EmployeesPage /></ProtectedRoute>} />
          <Route path="/admin/departments" element={<ProtectedRoute allowedRoles={['ADMIN']}><DepartmentsPage /></ProtectedRoute>} />
          <Route path="/admin/leave-types" element={<ProtectedRoute allowedRoles={['ADMIN']}><LeaveTypesPage /></ProtectedRoute>} />
          <Route path="/admin/leave-balances" element={<ProtectedRoute allowedRoles={['ADMIN']}><LeaveBalancesPage /></ProtectedRoute>} />
          <Route path="/admin/leave-requests" element={<ProtectedRoute allowedRoles={['ADMIN']}><LeaveRequestsPage /></ProtectedRoute>} />
          <Route path="/admin/audit-logs" element={<ProtectedRoute allowedRoles={['ADMIN']}><AuditLogsPage /></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
