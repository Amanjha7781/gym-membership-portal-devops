# Final Project Report — CI/CD Pipeline for a Gym Membership Portal

**Student:** AMANKUMAR JHA<br>
**Roll No:** 23102C0072<br>
**Verification date:** 2026-10-19

## 1. Problem statement and objectives

Small gyms may keep member details in registers or spreadsheets. Records can become difficult to search and update, and membership status can be missed. This project provides a small authenticated portal for managing members and demonstrates automated build, test, container packaging, deployment, configuration management, and recovery.

The objectives are to register, list, search, and update members; manage membership plans and status; present dashboard counts; restrict administrative actions; persist member data; and demonstrate a local CI/CD workflow.

## 2. Requirements and user roles

Staff can register, search, view, and update records. Admins can also change membership status and view summary counts. The application validates submitted data and exposes a REST API with role checks. Detailed requirements are in [Requirements and Architecture](Requirements_Architecture.md) and [Agile Planning](Agile_Planning.md).

## 3. Architecture

The Spring Boot application uses controller, service, repository, and model layers. Thymeleaf templates and CSS provide the browser interface. Spring Security provides admin and staff roles. Spring Data JPA stores members in MySQL 8.4. The application and database run as separate Docker Compose services on a private project network; MySQL data lives in the named volume `gym-membership-portal-mysql`.

Jenkins runs in a separate Compose service with persistent home storage. The project is mounted read-only at `/workspace`; its Pipeline checks out the local repository, runs Maven tests and Selenium, builds a Docker image, and conditionally deploys from main. Ansible runs in a disposable Linux container on Windows and manages the local Docker engine. See [Architecture](Architecture.md), [Docker](Docker.md), [Jenkins](Jenkins.md), and the [Ansible guide](../deployment/ansible/README.md).

## 4. Technology stack and data

The stack is JDK 21, Spring Boot 3.5.8, Maven 3.9.16, MySQL 8.4, Spring Security, Thymeleaf, JUnit 5, Selenium 4.49, Docker Desktop/Compose, Jenkins LTS, Ansible, and Git. Runtime credentials are provided through the ignored local `.env`; no values are included in this report. The application uses H2 for automated integration tests and MySQL for the Docker deployment.

## 5. Application implementation

The MVP implements member creation, viewing, editing, search by ID/name/email, plan and status selection, summary counts, REST routes, and admin/staff login. Admin-only status change is enforced. Main source is under `src/main`; tests are under `src/test`. The optional Selenium flow covers login, create, search, edit, and status update.

## 6. CI/CD and testing results

The Jenkins Pipeline stages are Checkout, Build and Unit Tests (`mvn -B clean verify`), Selenium End-to-End, Docker Build, and guarded Deploy. Jenkins publishes Maven and Selenium JUnit reports. The local repository mount allows Jenkins checkout without GitHub credentials.

**Historical Jenkins build #7** was inspected in Jenkins and completed `SUCCESS` on 2026-10-09. It checked out commit `0f6247d7811796ed2c62545c3ff6ffc7346ef46c`; Maven reported 8 tests (7 passed, 1 opt-in Selenium test skipped, 0 failures/errors); the separate Selenium stage passed 1 test; Docker image build and deployment completed; the app health endpoint returned HTTP 200 / `UP`. The screenshots `jenkins-build-7.png.png` and `jenkins-test-results.png.png` are genuine historical evidence. Build #7 is not a fresh build of the current working tree.

A fresh local `mvn -B verify` passed on 2026-10-19: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. The report is [maven-verify-20261019-115032.txt](task15-evidence/maven-verify-20261019-115032.txt). A separate live Selenium run passed 1 test against the MySQL-backed Docker app. It created one synthetic test member, searched and edited it, changed its status to `INACTIVE`, and captured genuine screenshots. Its record was verified in MySQL, persisted through an app-only restart, then deleted; a query confirmed zero remaining for that test ID. The exact run record is [persistence-20261019-115528.txt](task15-evidence/persistence-20261019-115528.txt).

A fresh Jenkins build could not be triggered: Jenkins is running, the local job configuration is present, but the anonymous job API returned HTTP 403 and requires an interactive sign-in. `nextBuildNumber` is 8; no result for build #8 is claimed. Sign in at <http://localhost:8081> and trigger it to complete this verification.

## 7. Docker deployment and current health

Docker Desktop engine 29.8.0 is running. The current application container is healthy on image `gym-membership-portal:1.0.0`; MySQL 8.4 is healthy; the app health endpoint returned HTTP 200 with `{"status":"UP"}`. The current result is saved in [docker-current-status-20261019.txt](task15-evidence/docker-current-status-20261019.txt).

The persistence test restarted only the application container. MySQL remained healthy with the same container identity and the same volume mounted at `/var/lib/mysql`. A fresh logical SQL backup was created outside the repository at `%LOCALAPPDATA%\Gym-Membership-Portal\database-backups\gym_portal-task15-20261019-114451.sql`. SQL structure, row data, completion marker, and SHA-256 were checked. Because `gym_user` lacks the MySQL `PROCESS` privilege, the dump used `--no-tablespaces`. It was not restored into another schema. The backup contains database data and must stay private; it is not part of the submission. No database reset or MySQL container restart was performed.

## 8. Ansible provisioning, reliability, and rollback

Task 13 uses the local inventory and `site.yml` configuration playbook through `deployment/ansible/`. Its initial failed attempt and corrected successful execution are retained; the corrected rerun and idempotence logs passed.

Task 14 adds preflight checks, safe provisioning, health checks, known-good image snapshot, app recovery, and rollback. Historical run `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log` records the controlled app-only recovery and unhealthy-candidate rollback. It restored the known-good image and HTTP 200 / `UP`; MySQL identity and volume were preserved. That deliberate failure exercise was not repeated during this final verification.

Fresh checks on 2026-10-19 validated Compose configuration, inventory, and syntax for `site.yml`, `provision.yml`, `snapshot.yml`, and `rollback.yml`. The safe `provision.yml` playbook ran twice against the already healthy services. Both runs reported `changed=0`, `failed=0`; the existing MySQL container and named volume stayed unchanged. Logs are in `task15-evidence/ansible-validation-20261019.txt`, `ansible-provision-20261019-115311.txt`, and `ansible-idempotence-20261019-115311.txt`.

## 9. Evidence and submission status

Seven readable screenshots are present: the original sign-in page, Jenkins build #7 summary and aggregate test report, plus live dashboard, member list, edit form, and filtered status workflow screenshots from Selenium. The live UI screenshots show a synthetic test member and do not display `.env` or credentials. The Docker status log is available, but a Docker GUI/terminal screenshot is not. The Jenkins stage-overview screenshot and fresh build evidence are also missing.

Task-by-task locations and exact qualifications are in the [Evidence Index](Evidence_Index.md); capture status is in the [Evidence Checklist](Evidence_Checklist.md).

## 10. Limitations and remaining actions

- A local commit (`209c2c0`) contains the verified project and evidence changes. No GitHub remote or webhook is configured. Task 4 is partial until the student selects/authenticates a repository; no URL is claimed.
- Jenkins build #7 is historical. A fresh build #8, its console, and stage screenshot require the student's interactive Jenkins sign-in.
- A Docker status screenshot is still needed; text output verifies current healthy containers and image.
- The current database has pre-existing test data from earlier runs. This completion run did not remove it; only the new fixture created by the persistence test was deleted.
- This project is a local lab deployment, not a public service. HTTPS, managed secrets, production monitoring, schema migrations, and a formal backup/restore policy are outside the MVP.
- No full MySQL container restart was performed. Persistence was verified across an application-container restart with MySQL left healthy and unchanged.

## 11. Conclusion

The MVP, local Docker deployment, fresh Maven and Selenium checks, MySQL persistence through an app-only restart, and Ansible provisioning/idempotence are verified. Historical Jenkins build #7 and Task 14 recovery/rollback evidence remain valid historical results. The current app and MySQL are healthy; Jenkins is running but requires sign-in for a fresh build. **Overall submission readiness is not yet complete**: Task 4 (GitHub) and Task 15 (authenticated Jenkins evidence and student review) remain partial. Do not submit the private SQL backup.

The full point-in-time audit summary is [Task15_Final_Audit_20261019.md](task15-evidence/Task15_Final_Audit_20261019.md); the earlier runtime audit remains as a historical record.
