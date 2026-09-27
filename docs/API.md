# API Reference (summary)

Full interactive docs are served by the running backend at `/swagger-ui.html`
(OpenAPI JSON at `/v3/api-docs`). This file is a quick static summary.

All non-auth endpoints require `Authorization: Bearer <token>`.

## Auth
| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |

## Employee self-service
| Method | Path | Access |
|---|---|---|
| GET | /api/employees/me | Authenticated |
| PUT | /api/employees/me | Authenticated |
| GET | /api/employees/dashboard | Authenticated (employee view) |
| POST | /api/leaves | Authenticated |
| GET | /api/leaves/my | Authenticated |
| GET | /api/leaves/balance | Authenticated |
| GET | /api/leaves/{id} | Owner, their manager, or Admin |
| PUT | /api/leaves/{id}/cancel | Owner only |

## Manager
| Method | Path | Access |
|---|---|---|
| GET | /api/manager/dashboard | MANAGER, ADMIN |
| GET | /api/manager/team | MANAGER, ADMIN |
| GET | /api/manager/leaves | MANAGER, ADMIN |
| PUT | /api/manager/leaves/{id}/approve | MANAGER, ADMIN (own team only) |
| PUT | /api/manager/leaves/{id}/reject | MANAGER, ADMIN (own team only) |

## Admin
| Method | Path | Access |
|---|---|---|
| GET/POST | /api/admin/employees | ADMIN |
| GET/PUT/DELETE | /api/admin/employees/{id} | ADMIN |
| GET/POST | /api/admin/departments | ADMIN |
| GET/PUT/DELETE | /api/admin/departments/{id} | ADMIN |
| GET/POST | /api/admin/leave-types | ADMIN |
| GET/PUT/DELETE | /api/admin/leave-types/{id} | ADMIN |
| GET/POST | /api/admin/leave-balances | ADMIN |
| PUT/DELETE | /api/admin/leave-balances/{id} | ADMIN |
| GET | /api/admin/leaves | ADMIN |
| GET | /api/admin/leaves/{id} | ADMIN |
| GET | /api/admin/dashboard | ADMIN |
| GET | /api/admin/audit-logs | ADMIN |

## Error format
```json
{
  "timestamp": "2026-09-26T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Start date cannot be after end date",
  "path": "/api/leaves",
  "fieldErrors": { "email": "Email must be valid" }
}
```
`fieldErrors` is only present for validation (`400`) responses.
