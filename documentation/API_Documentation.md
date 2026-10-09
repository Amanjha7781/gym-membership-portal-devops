# API Documentation

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

All member endpoints require HTTP Basic or a logged-in session with `ADMIN` or `STAFF`. The `PUT .../status` endpoint requires `ADMIN`. JSON dates use ISO `YYYY-MM-DD`; enum values are uppercase.

| Method | Path | Description |
|---|---|---|
| GET | `/api/members` | List members; optional `?search=...` matches name, email, or numeric ID |
| GET | `/api/members/{id}` | Find one member |
| POST | `/api/members` | Create a member |
| PUT | `/api/members/{id}` | Update all member fields |
| PUT | `/api/members/{id}/status?value=ACTIVE` | Change status (admin only) |
| GET | `/actuator/health` | Health endpoint (public, minimal status only) |

Example member JSON:

```json
{
  "name": "Sample Member",
  "email": "sample@example.test",
  "phone": "+91 98765 43210",
  "plan": "MONTHLY",
  "startDate": "2026-10-01",
  "endDate": "2026-11-01",
  "status": "ACTIVE"
}
```

Successful create/update requests return the saved member as JSON. Validation errors return HTTP 400. Missing IDs return 404. Unauthorized requests are redirected to login for browser routes; access-controlled API calls return an authorization response.

The dashboard UI uses the same service layer to calculate total and per-status counts. Search is available from the members table.

