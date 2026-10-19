# Testing

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

## Automated tests

- `MemberServiceTest` checks member create, update, and status/summary counts against an H2 JPA repository.
- `MemberPortalIntegrationTest` checks Spring MVC/API create, retrieve, search, update, input validation, role-gated status changes, and unauthenticated login redirection.
- `MemberPortalSeleniumTest` is opt-in and exercises login, create, view, search, edit, and status change in a real browser.

Run unit/service/integration tests:

```powershell
mvn clean verify
```

The test report is generated under `target/surefire-reports/`.

## Selenium

Start the MySQL and application containers, then set `GYM_ADMIN_PASSWORD` in the current PowerShell session to the same private value as `.env`:

```powershell
$env:GYM_ADMIN_PASSWORD = 'your-local-admin-password'
$env:PORTAL_BASE_URL = 'http://localhost:8080'
mvn -DrunSelenium=true -Dtest=MemberPortalSeleniumTest test
```

Selenium Manager locates Chrome and downloads a matching ChromeDriver as needed. For Jenkins or another remote browser, set `SELENIUM_REMOTE_URL` and `PORTAL_BASE_URL` to URLs resolvable from the test runner. The Compose `e2e` profile supplies a pinned Selenium Grid image; ensure the Docker engine has enough memory for MySQL, Jenkins, and Chrome.

The UI test adds one synthetic test member to the configured database and updates its status. It does not automatically clean the database. In the recorded Task 15 persistence check, the newly created row was identified by its new database ID, verified after an app-only restart, then deleted by that exact ID; the database query confirmed no row remained. Existing records were left unchanged. Set `PORTAL_EVIDENCE_DIR` only when genuine browser screenshots are wanted; the test then saves the dashboard, registration/search result, edit form, and updated-status screenshots there. Those screenshots use a unique test email and do not include `.env` values.

