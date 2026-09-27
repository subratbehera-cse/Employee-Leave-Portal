import api from './axiosConfig';

// Auth
export const registerUser = (data) => api.post('/auth/register', data);
export const loginUser = (data) => api.post('/auth/login', data);

// Employee
export const getMyProfile = () => api.get('/employees/me');
export const updateMyProfile = (data) => api.put('/employees/me', data);
export const getEmployeeDashboard = () => api.get('/employees/dashboard');

// Leave (employee)
export const applyLeave = (data) => api.post('/leaves', data);
export const getMyLeaves = () => api.get('/leaves/my');
export const getMyBalances = () => api.get('/leaves/balance');
export const getLeaveById = (id) => api.get(`/leaves/${id}`);
export const cancelLeave = (id) => api.put(`/leaves/${id}/cancel`);

// Manager
export const getManagerDashboard = () => api.get('/manager/dashboard');
export const getTeamMembers = () => api.get('/manager/team');
export const getTeamLeaves = () => api.get('/manager/leaves');
export const approveLeave = (id, data) => api.put(`/manager/leaves/${id}/approve`, data || {});
export const rejectLeave = (id, data) => api.put(`/manager/leaves/${id}/reject`, data || {});

// Admin - dashboard
export const getAdminDashboard = () => api.get('/admin/dashboard');

// Admin - employees
export const getAllEmployees = () => api.get('/admin/employees');
export const getEmployeeByIdAdmin = (id) => api.get(`/admin/employees/${id}`);
export const createEmployee = (data) => api.post('/admin/employees', data);
export const updateEmployee = (id, data) => api.put(`/admin/employees/${id}`, data);
export const deleteEmployee = (id) => api.delete(`/admin/employees/${id}`);

// Admin - departments
export const getDepartments = () => api.get('/admin/departments');
export const createDepartment = (data) => api.post('/admin/departments', data);
export const updateDepartment = (id, data) => api.put(`/admin/departments/${id}`, data);
export const deleteDepartment = (id) => api.delete(`/admin/departments/${id}`);

// Admin - leave types
export const getLeaveTypes = () => api.get('/admin/leave-types');
export const createLeaveType = (data) => api.post('/admin/leave-types', data);
export const updateLeaveType = (id, data) => api.put(`/admin/leave-types/${id}`, data);
export const deleteLeaveType = (id) => api.delete(`/admin/leave-types/${id}`);

// Admin - leave balances
export const getAllLeaveBalances = () => api.get('/admin/leave-balances');
export const createLeaveBalance = (data) => api.post('/admin/leave-balances', data);
export const updateLeaveBalance = (id, data) => api.put(`/admin/leave-balances/${id}`, data);
export const deleteLeaveBalance = (id) => api.delete(`/admin/leave-balances/${id}`);

// Admin - all leave requests
export const getAllLeaveRequestsAdmin = () => api.get('/admin/leaves');
export const getLeaveRequestByIdAdmin = (id) => api.get(`/admin/leaves/${id}`);

// Admin - audit logs
export const getAuditLogs = (page = 0, size = 20) => api.get(`/admin/audit-logs?page=${page}&size=${size}`);
