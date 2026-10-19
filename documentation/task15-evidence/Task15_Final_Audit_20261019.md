# Task 15 Final Audit — 2026-10-19

**Project:** CI/CD Pipeline for a Gym Membership Portal
**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072
**Workspace:** `C:\Users\Divya\OneDrive\Desktop\Gym-Membership-Portal`

## Environment and live runtime

- Windows project directory and existing Git checkout verified; current branch is `main`.
- Java/JDK: 21.0.12.1 (64-bit); Maven: 3.9.16; Git: 2.55.0.windows.1; Docker client and engine: 29.8.0.
- Docker Desktop was stopped at the beginning of the audit. Its existing per-user executable was started. Existing containers and volumes came up; no reinstall, compose down, rebuild, database reset, or volume operation was performed.
- `deployment-gym-portal-1` and `deployment-mysql-1` are running and healthy; Jenkins `gym-portal-jenkins` is running. The app endpoint returned HTTP 200 and `{"status":"UP"}`. Detailed current state is in `docker-current-status-20261019.txt`.
- Jenkins authenticated browser session confirmed. Before triggering, queue was empty and executors were idle. Build #8 checked out `b98d762bfe621fb263c2a37e1efac47a2c06c34a` and finished `SUCCESS` in 1m 58s; its full console and stage view were inspected. See `jenkins-build-8-results-20261019.txt`.

## Tests and data preservation

- `mvn -B verify` passed from the current test source: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. Full output: `maven-verify-20261019-115032.txt`. This suite uses H2.
- The live Selenium test passed 1 test against the MySQL-backed app, covering authentication, member creation, search, editing, and status update. Its execution and persistence results are recorded in `persistence-20261019-115528.txt`.
- The fresh synthetic fixture (database ID 15) was present with `INACTIVE` status before the app-only restart and remained present after restart. App health returned HTTP 200 / `UP`; MySQL stayed healthy with the same container identity and volume mount `gym-membership-portal-mysql:/var/lib/mysql`. The new fixture was then deleted by its exact ID and a query verified zero remaining. Existing records were not removed or changed by this operation.
- A fresh logical MySQL backup exists outside the project at `%LOCALAPPDATA%\Gym-Membership-Portal\database-backups\gym_portal-task15-20261019-114451.sql`. The SQL dump includes database/table definitions and row data, ends with the dump completion marker, and has a recorded SHA-256. `--no-tablespaces` was used because the app account lacks `PROCESS`. The dump was not restored to a second schema; no MySQL service or volume was reset.
- Fresh Ansible validation (Compose, inventory, and syntax of `site.yml`, `provision.yml`, `snapshot.yml`, and `rollback.yml`) passed. The safe provision playbook ran twice against the healthy stack; each recap was `changed=0`, `failed=0`. Logs: `ansible-validation-20261019.txt`, `ansible-provision-20261019-115311.txt`, and `ansible-idempotence-20261019-115311.txt`.
- Historical Task 14 app recovery and rollback evidence remains `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log`. The deliberate unhealthy-image exercise was not repeated.

## Genuine screenshot evidence

The original `application.png.png`, `jenkins-build-7.png.png`, and `jenkins-test-results.png.png` files remain unchanged. Fresh Selenium captures were saved and visually inspected: `task15-dashboard.png`, `task15-member-registration.png`, `task15-member-edit-form.png`, and `task15-member-workflow.png`. The app images show the synthetic filtered test member only; the record was subsequently removed. At the time this audit was first written, build #8 and Docker screenshots had not yet been persisted; see the follow-up verification below for the updated evidence state.

## Task completion matrix

| Task range | Status | Basis |
|---|---|---|
| 1–3 | COMPLETE | Scope, planning, requirements, and architecture docs are present. |
| 4 | PARTIAL | Local Git repository/commit exists, but no remote or GitHub URL is configured. |
| 5–10 | COMPLETE | MVP implementation; Maven/H2 checks; fresh Selenium flow; historical Jenkins #7 and fresh Jenkins #8 CI results. |
| 11 | COMPLETE | Docker app/MySQL, named volume, and MySQL-backed record persistence across an app-only restart verified. MySQL itself was not restarted. |
| 12–14 | COMPLETE | Jenkins #8 deployment and health verified; Task 13 configuration management; Task 14 historical recovery/rollback plus fresh safe idempotent provisioning. |
| 15 | PARTIAL | Report/index/checklist and Jenkins #8 results are updated; stage/test and Docker screenshots are present. Student review and any required GitHub setup remain. |

**At the initial audit point: 13 of 15 tasks complete; Tasks 4 and 15 remain partial.** No completion is inferred from documentation alone.

## Git, secrets, and publication

- `.env` is ignored and not tracked. The database backup is outside the project. No secret values were intentionally written to logs, screenshots, or documentation. Trailing horizontal whitespace and redundant final blank lines were trimmed from text log copies for `git diff --check`; command text and recorded results were unchanged.
- Seven older screenshots are tracked. The three additional Jenkins #8/Docker screenshots are explicitly allowed by `.gitignore` and are untracked, ready to stage; other screenshot directory contents remain ignored by default.
- No Git remote is configured. No GitHub URL exists in the report. No push/publication was performed.
- Local project changes and evidence were committed on `main` in commit `209c2c0`; no remote was configured and nothing was pushed. Keep `.env` and the SQL backup private and excluded.

## Remaining actions at initial audit time (superseded by follow-up below)

1. Save genuine screenshots of the completed build #8 summary, Pipeline Overview, and test report as `jenkins-build-8.png`, `jenkins-build-8-stages.png`, and `jenkins-build-8-test-results.png` under `screenshots/`. The run itself is complete; do not trigger a duplicate build for evidence.
2. Capture a genuine Docker view showing healthy app/MySQL containers, the app image, and HTTP 200 / `UP` health.
3. If the assignment requires GitHub, create/select the repository under the student's account, then provide the real remote and approve publication.
4. Review the final report and evidence package before submitting to the professor.

The original `Task15_Audit_20261019.md` remains as the earlier point-in-time audit that recorded Docker Desktop stopped. This file records the later restoration and verification in the same day.

## Follow-up read-only audit — 2026-10-19

- Reopened the authenticated Jenkins build #8 page: it showed `SUCCESS`, revision `b98d762bfe621fb263c2a37e1efac47a2c06c34a`, and `Tests (no failures)`. No build was triggered.
- Verified all ten current screenshot files were readable. New persistent evidence is `screenshots/jenkins-build-8-stages.png`, `screenshots/jenkins-build-8-test-results.png`, and `screenshots/docker-containers.png`. These three screenshots are explicitly allowed by `.gitignore` and are currently untracked, ready to stage. The stage/test screenshots and Docker GUI evidence are therefore no longer missing. A separate #8 summary screenshot and full console export are absent.
- Docker daemon reports 29.8.0 with 3 running containers. `deployment-gym-portal-1` and `deployment-mysql-1` are healthy. Running app image ID equals the `gym-membership-portal:1.0.0` image ID (`sha256:11294f387c4c53985ae50a3f611fb1f1988623bc399c1cf57f061a916e6a461d`). `http://localhost:8080/actuator/health` returned HTTP 200 with `{"status":"UP"}`. No services were restarted.
- A new `mvn -B verify` run passed: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. This output was observed in the terminal and was not saved as a new log file.
- `git diff --check` passed. `.env` remains ignored and untracked; tracked-file scan found no runtime environment file, database dump/backup, private key, or certificate. Git remote remains empty. The three new screenshots are allowed by `.gitignore` and untracked, ready to stage; no ignore-rule change is pending.
- Task 4 and Task 15 remain partial. Task 15 now only needs student review and any required GitHub repository/remote work; the optional separate build-summary image/full console export can be captured if the rubric asks for them. No remote was configured and nothing was pushed.
