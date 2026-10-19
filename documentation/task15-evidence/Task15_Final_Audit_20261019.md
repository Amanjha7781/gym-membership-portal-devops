# Task 15 Final Audit — 2026-10-19

**Project:** CI/CD Pipeline for a Gym Membership Portal
**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072
**Workspace:** `C:\Users\Divya\OneDrive\Desktop\Gym-Membership-Portal`

## Environment and live runtime

- Windows project directory and existing Git checkout verified; current branch is `main`.
- Java/JDK: 21.0.12.1 (64-bit); Maven: 3.9.16; Git: 2.55.0.windows.1; Docker client and engine: 29.8.0.
- Docker Desktop was stopped at the beginning of the audit. Its existing per-user executable was started. Existing containers and volumes came up; no reinstall, compose down, rebuild, database reset, or volume operation was performed.
- `deployment-gym-portal-1` and `deployment-mysql-1` are running and healthy; Jenkins `gym-portal-jenkins` is running. The app endpoint returned HTTP 200 and `{"status":"UP"}`. Detailed current state is in `docker-current-status-20261019.txt`.
- Jenkins job config exists; build #7 is `SUCCESS`; its persisted next build number is 8. Jenkins login page returns HTTP 200 and anonymous job API returns HTTP 403. A fresh build was not triggered because an authenticated Jenkins session was unavailable. See `jenkins-current-status-20261019.txt`.

## Tests and data preservation

- `mvn -B verify` passed from the current test source: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. Full output: `maven-verify-20261019-115032.txt`. This suite uses H2.
- The live Selenium test passed 1 test against the MySQL-backed app, covering authentication, member creation, search, editing, and status update. Its execution and persistence results are recorded in `persistence-20261019-115528.txt`.
- The fresh synthetic fixture (database ID 15) was present with `INACTIVE` status before the app-only restart and remained present after restart. App health returned HTTP 200 / `UP`; MySQL stayed healthy with the same container identity and volume mount `gym-membership-portal-mysql:/var/lib/mysql`. The new fixture was then deleted by its exact ID and a query verified zero remaining. Existing records were not removed or changed by this operation.
- A fresh logical MySQL backup exists outside the project at `%LOCALAPPDATA%\Gym-Membership-Portal\database-backups\gym_portal-task15-20261019-114451.sql`. The SQL dump includes database/table definitions and row data, ends with the dump completion marker, and has a recorded SHA-256. `--no-tablespaces` was used because the app account lacks `PROCESS`. The dump was not restored to a second schema; no MySQL service or volume was reset.
- Fresh Ansible validation (Compose, inventory, and syntax of `site.yml`, `provision.yml`, `snapshot.yml`, and `rollback.yml`) passed. The safe provision playbook ran twice against the healthy stack; each recap was `changed=0`, `failed=0`. Logs: `ansible-validation-20261019.txt`, `ansible-provision-20261019-115311.txt`, and `ansible-idempotence-20261019-115311.txt`.
- Historical Task 14 app recovery and rollback evidence remains `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log`. The deliberate unhealthy-image exercise was not repeated.

## Genuine screenshot evidence

The original `application.png.png`, `jenkins-build-7.png.png`, and `jenkins-test-results.png.png` files remain unchanged. Fresh Selenium captures were saved and visually inspected: `task15-dashboard.png`, `task15-member-registration.png`, `task15-member-edit-form.png`, and `task15-member-workflow.png`. The app images show the synthetic filtered test member only; the record was subsequently removed. No Docker GUI/terminal screenshot or Jenkins stage-view screenshot has been captured.

## Task completion matrix

| Task range | Status | Basis |
|---|---|---|
| 1–3 | COMPLETE | Scope, planning, requirements, and architecture docs are present. |
| 4 | PARTIAL | Local Git repository/commit exists, but no remote or GitHub URL is configured. |
| 5–10 | COMPLETE | MVP implementation; Maven/H2 checks; fresh Selenium flow; historical Jenkins #7 CI result. |
| 11 | COMPLETE | Docker app/MySQL, named volume, and MySQL-backed record persistence across an app-only restart verified. MySQL itself was not restarted. |
| 12–14 | COMPLETE | Historical successful Jenkins deployment; Task 13 configuration management; Task 14 historical recovery/rollback plus fresh safe idempotent provisioning. |
| 15 | PARTIAL | Report/index/checklist and evidence were updated; fresh authenticated Jenkins run/stage view, Docker screenshot, and student review remain. |

**Overall: 13 of 15 tasks complete; Tasks 4 and 15 remain partial.** No completion is inferred from documentation alone.

## Git, secrets, and publication

- `.env` is ignored and not tracked. The database backup is outside the project. No secret values were intentionally written to logs, screenshots, or documentation.
- Seven genuine screenshots are allowed by explicit `.gitignore` entries; no other screenshot directory contents are included by default.
- No Git remote is configured. No GitHub URL exists in the report. No push/publication was performed.
- Local repository changes are prepared for a local commit; review the exact staged diff before submission. Keep `.env` and the SQL backup private and excluded.

## Remaining actions

1. Sign in to Jenkins directly at <http://localhost:8081>, run build #8, inspect its actual console and stage overview, and capture the current test results. Do not share credentials in chat.
2. Capture a genuine Docker view showing healthy app/MySQL containers, the app image, and HTTP 200 / `UP` health.
3. If the assignment requires GitHub, create/select the repository under the student's account, then provide the real remote and approve publication.
4. Review the final report and evidence package before submitting to the professor.

The original `Task15_Audit_20261019.md` remains as the earlier point-in-time audit that recorded Docker Desktop stopped. This file records the later restoration and verification in the same day.
