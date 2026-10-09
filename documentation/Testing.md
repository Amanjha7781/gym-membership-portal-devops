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

The UI test adds one test member to the local database. It does not clean or delete data. Review and remove test records through the app if desired.

