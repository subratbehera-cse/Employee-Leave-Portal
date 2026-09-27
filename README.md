# Employee Leave Management Portal

A production-style, full-stack HR portal for managing employee leave: applications, manager
approvals, leave balances, and admin oversight — built with Spring Boot (Java 21) and React.

> **Build note:** this project was generated in a sandboxed environment whose network egress
> does not include Maven Central, so the backend could not be compiled/tested with `mvn` in
> that environment. The frontend **was** built successfully with `npm run build` (Vite,
> 124 modules, zero errors). Every backend file was manually reviewed for correctness, but
> please run `mvn clean install` yourself as the first step after downloading — see
> [Verifying the build](#verifying-the-build) below.

---

## 1. Overview

Three roles share one portal:

- **Employees** apply for leave, track balances, and view their request history.
- **Managers** review their direct reports' requests and approve or reject them with remarks.
- **Admins** manage employees, departments, leave types, leave balances, and can see every
  request and a full audit trail.

## 2. Features

- JWT-based authentication with BCrypt password hashing and role-based authorization
- Leave application with automatic day calculation, overlap prevention, and balance checks
- Manager approval workflow (cannot approve own leave; can only act on their own team)
- Balance is deducted on approval and restored automatically on cancellation of an approved leave
- Full admin CRUD for employees, departments, leave types, and leave balances
- Company-wide audit log of key actions (register, login, apply, approve, reject, cancel, admin edits)
- Role-scoped dashboards with live statistics
- Centralized error handling with consistent JSON error responses
- Swagger/OpenAPI documentation

## 3. Tech Stack

**Backend:** Java 21, Spring Boot 3.3, Spring Web, Spring Security, JWT (jjwt), Spring Data
JPA/Hibernate, MySQL, Maven, Lombok, Bean Validation, BCrypt, springdoc-openapi.

**Frontend:** React 18 + Vite, React Router, Axios, plain CSS (no UI framework) with a custom
design system.

## 4. Architecture

```
backend/
  src/main/java/com/leavemanagement/
    controller/    HTTP endpoints
    service/       Business logic interfaces
    service/impl/  Business logic implementations
    repository/    Spring Data JPA repositories
    entity/        JPA entities
    dto/           Request/response payloads (entities are never exposed directly)
    mapper/        Entity <-> DTO mapping
    security/      JWT filter, JwtUtil, UserDetails, entry points
    exception/     Custom exceptions + GlobalExceptionHandler
    config/        Security, CORS, OpenAPI, startup data seeding
    enums/         Role, LeaveStatus, EmployeeStatus
frontend/
  src/
    api/           Axios instance + one function per endpoint
    context/       AuthContext (JWT/session state)
    components/    Sidebar, Navbar, Table, Modal, StatusBadge, etc.
    pages/         auth/ employee/ manager/ admin/
```

## 5. Database / ER Overview

| Table            | Purpose                                                             |
|------------------|----------------------------------------------------------------------|
| `users`          | Employees/managers/admins; self-referencing `manager_id`            |
| `departments`    | Organizational departments                                          |
| `leave_types`    | Configurable leave categories with a default annual allocation      |
| `leave_balances` | Per-user, per-type, per-year total/used/remaining day counts        |
| `leave_requests` | Individual leave applications and their lifecycle                   |
| `audit_logs`     | Append-only record of significant actions                           |

Relationships are intentionally one-directional (`@ManyToOne` only) to avoid unnecessary
bidirectional JPA complexity. See `database/schema.sql` for full DDL.

Leave request lifecycle: `PENDING → APPROVED | REJECTED`, and `PENDING | APPROVED → CANCELLED`.
Approving a request deducts from the employee's balance; cancelling a previously approved
request restores it.

## 6. API List

All endpoints are prefixed with `/api`. Full interactive documentation is at
`http://localhost:8080/swagger-ui.html` once the backend is running.

**Auth** (public): `POST /auth/register`, `POST /auth/login`

**Employee**: `GET /employees/me`, `PUT /employees/me`, `GET /employees/dashboard`

**Leave (self-service)**: `POST /leaves`, `GET /leaves/my`, `GET /leaves/balance`,
`GET /leaves/{id}`, `PUT /leaves/{id}/cancel`

**Manager**: `GET /manager/dashboard`, `GET /manager/team`, `GET /manager/leaves`,
`PUT /manager/leaves/{id}/approve`, `PUT /manager/leaves/{id}/reject`

**Admin**: `GET|POST /admin/employees`, `GET|PUT|DELETE /admin/employees/{id}`,
`GET|POST /admin/departments`, `GET|PUT|DELETE /admin/departments/{id}`,
`GET|POST /admin/leave-types`, `GET|PUT|DELETE /admin/leave-types/{id}`,
`GET|POST /admin/leave-balances`, `PUT|DELETE /admin/leave-balances/{id}`,
`GET /admin/leaves`, `GET /admin/leaves/{id}`, `GET /admin/dashboard`,
`GET /admin/audit-logs?page=&size=`

## 7. Setup Instructions

### Prerequisites
- Java 21 (JDK)
- Maven 3.9+
- Node.js 18+ and npm
- MySQL 8+

### MySQL configuration
```sql
CREATE DATABASE leave_management_db;
```
The application will also auto-create the database on first connect
(`createDatabaseIfNotExist=true`), and Hibernate will create/update the tables
(`ddl-auto=update`). `database/schema.sql` and `database/data.sql` are provided for manual
setup or reference.

Copy `.env.example` to `.env` (or export the variables directly) and set at least
`DB_USERNAME`, `DB_PASSWORD`, and a strong `JWT_SECRET`. **Never commit real secrets.**

### Backend — run command
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The API starts on `http://localhost:8080`. On first boot, `DataInitializer` seeds three
departments, three leave types, and three demo accounts (see below) with initial leave
balances — so you can log in immediately without writing any SQL by hand.

### Frontend — run command
```bash
cd frontend
npm install
cp .env.example .env   # adjust VITE_API_BASE_URL if the backend isn't on localhost:8080
npm run dev
```
The app starts on `http://localhost:5173`.

### Demo credentials
| Role     | Email                  | Password      |
|----------|-------------------------|---------------|
| Admin    | admin@company.com      | Admin@123     |
| Manager  | manager@company.com    | Manager@123   |
| Employee | employee@company.com   | Employee@123  |

## 8. Verifying the build

This project was built and reviewed carefully, but the sandbox it was authored in could not
reach Maven Central to run `mvn compile`/`mvn test` (only npm/PyPI/crates/GitHub were
reachable). Before relying on it:

1. `cd backend && mvn clean install` — resolves dependencies, compiles, and runs the
   JUnit/Mockito test suite (`AuthServiceImplTest`, `LeaveServiceImplTest`,
   `ManagerServiceImplTest`, `AuthIntegrationTest`).
2. `mvn spring-boot:run` and confirm it connects to MySQL and seeds demo data.
3. `cd frontend && npm install && npm run build` — this **was** verified successfully in the
   authoring environment (124 modules, no errors).
4. Log in as each of the three demo users and walk through: apply → approve/reject → cancel →
   admin employee/department/leave-type/balance CRUD → audit log.

If `mvn clean install` surfaces anything, the most likely spots (given no compiler was
available while authoring) are minor Lombok-generated accessor name mismatches — grep the
stack trace's field name across `entity/`, `dto/`, and the file that calls it.

## 9. Future Improvements

- Refresh tokens / token revocation list
- Email notifications on leave status changes
- Multi-level (multi-manager) approval chains
- Public holiday calendar integration when calculating leave days
- Pagination on employee/leave-request admin list views
- CI pipeline (GitHub Actions) running `mvn test` and `npm run build` on every push

## 10. Security Notes

- Passwords are BCrypt-hashed; the API never returns a password field.
- JWT contains user id, role and expiration; validated on every request via a
  `OncePerRequestFilter`.
- Endpoint-level authorization is enforced in `SecurityConfig` (`/api/admin/**` requires
  `ADMIN`, `/api/manager/**` requires `MANAGER` or `ADMIN`) and re-checked in service methods
  (e.g., a manager can only approve requests from users whose `manager_id` matches their own).
- All secrets (`JWT_SECRET`, DB credentials) are read from environment variables — see
  `.env.example` — never hardcoded.
