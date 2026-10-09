# CI/CD Pipeline for a Gym Membership Portal

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

A college-scale Java/Spring Boot application for managing gym members, accompanied by a Maven test suite, Docker Compose deployment, Selenium browser flow and Jenkins pipeline.

## Features

- Admin and staff form login; passwords are supplied through environment variables and stored as BCrypt hashes in memory.
- Register, view, search and update members; search accepts a member ID, name, or email.
- Membership plans (monthly, quarterly, annual) and statuses (active, inactive, expired).
- Dashboard totals, including a count for each status.
- Admin-only status changes; staff can manage member details without changing status.
- REST endpoints, Jakarta Bean Validation, MySQL persistence, container health checks, and H2-backed automated tests.

## Requirements

- JDK 21, Maven 3.9+, Git, Docker Desktop with Linux containers and Compose v2.
- Internet access the first time Maven dependencies and container images are downloaded.
- Jenkins is optional for local app use and is started separately for the CI demonstration.

## Run locally with MySQL in Docker

From the repository root in PowerShell:

```powershell
Copy-Item .env.example .env
notepad .env  # Replace each placeholder with a different private local password.
mvn clean verify
docker compose -f deployment/docker-compose.yml up -d --build
docker compose -f deployment/docker-compose.yml ps
```

Wait until both services show healthy. Open <http://localhost:8080>. Sign in with username `admin` or `staff` and the corresponding `GYM_ADMIN_PASSWORD` or `GYM_STAFF_PASSWORD` set in `.env`. Do not share or commit `.env`.

```powershell
docker compose -f deployment/docker-compose.yml logs -f gym-portal
docker compose -f deployment/docker-compose.yml down
```

The named MySQL volume remains in place after `down`, so member data persists. To keep it, do not use `docker compose down -v`.

## Test

```powershell
mvn clean verify
```

This runs unit/service and Spring MVC/JPA integration tests with an in-memory H2 database. Selenium is opt-in and runs against the local portal in headless Chrome:

```powershell
$env:GYM_ADMIN_PASSWORD = 'use-the-same-value-as-.env'
$env:PORTAL_BASE_URL = 'http://localhost:8080'
mvn -DrunSelenium=true -Dtest=MemberPortalSeleniumTest test
```

The Selenium flow covers login, member creation, search, edit, and admin status update. ChromeDriver is resolved by Selenium Manager. A separate Selenium Grid service can be used by setting `SELENIUM_REMOTE_URL`; see [Testing](documentation/Testing.md).

## Jenkins

Jenkins is containerized separately and listens at <http://localhost:8081>. Start it with:

```powershell
docker compose -f deployment/jenkins/compose.yml up -d --build
```

The first-time Jenkins administrator unlock and plugin setup must be completed in the browser. Pipeline code is in [Jenkinsfile](Jenkinsfile). It runs Maven verification, the Selenium flow, a Docker build, then a guarded deployment stage on the `main` branch. Pipeline uses the ignored project `.env` file through a read-only `/workspace` mount; no password is committed to Git or configured as a Jenkins job literal. See [Jenkins](documentation/Jenkins.md).

## Repository layout

- `src/main/java` — Spring Boot controllers, service, JPA model/repository, and security configuration.
- `src/main/resources` — Thymeleaf UI and CSS.
- `src/test/java` — JUnit service/integration tests and an opt-in Selenium test.
- `deployment/docker-compose.yml` — app, MySQL, and optional Selenium Grid.
- `deployment/jenkins` — reproducible Jenkins image and Compose service.
- `deployment/scripts` — Windows PowerShell start/stop/status/restart helpers.
- `documentation` — architecture, API, database, tests, Docker, Jenkins, workflow, deployment, and final report.

See [Deployment](documentation/Deployment.md) for exact commands and [Evidence checklist](documentation/Evidence_Checklist.md) for items to capture during the lab demonstration.
