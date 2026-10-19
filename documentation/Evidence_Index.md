# Final Evidence Index and Task Status

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072
**Verification date:** 2026-10-19

This index compares the 15 assigned tasks with source files and actual execution evidence. A source file establishes implementation; a separate execution record establishes that it ran. `PARTIAL` means one or more assigned items still lack evidence or an external action. No GitHub URL is claimed.

## Assignment status

| Task | Status | Implementation and evidence | Gap or qualification |
|---|---|---|---|
| 1. Problem definition and scope | COMPLETE | `Project_Definition.md`, sections 1–8 | Scope, stakeholders, objectives, and success criteria are documented. |
| 2. Agile planning and DevOps workflow | COMPLETE | `documentation/Agile_Planning.md`, `documentation/CI_CD_Workflow.md` | Stories, backlog, workflow, and task mapping are documented. |
| 3. Requirements and architecture | COMPLETE | `documentation/Requirements_Architecture.md`, `Architecture.md`, `Database.md`, `API_Documentation.md`, `src/` | Architecture and implemented structure are present. |
| 4. Git and GitHub repository setup | PARTIAL | Local `main` repository and commit history; `.gitignore`; local commit `209c2c0` contains the project evidence and changes | `git remote -v` is empty. A GitHub repository, verified URL, and publication still require the student's account and approval. |
| 5. First feature development | COMPLETE | `src/main/java/edu/college/gymportal/`; `MemberPortalIntegrationTest`; Selenium flow | Member creation is implemented and tested. |
| 6. Complete MVP features | COMPLETE | Controllers, service, entities, templates, security, Maven tests; fresh Selenium flow | Create/view/update/search, status workflow, dashboard, validation, and admin/staff access were exercised. |
| 7. Jenkins installation and CI setup | COMPLETE | `deployment/jenkins/`; persisted `Gym-Membership-Portal-CI` job; successful builds #7 and #8 | Build #8 ran in the authenticated Jenkins session against the local Git repository without GitHub credentials. |
| 8. Jenkinsfile and server deployment | COMPLETE | `Jenkinsfile`; build #8 console/stage/test views; live Docker and health checks | Build #8 completed Maven, Selenium, Docker build, and Compose deployment; the app container and MySQL are healthy and the current app image matches the build output. |
| 9. Selenium test design | COMPLETE | `MemberPortalSeleniumTest.java`; `persistence-20261019-115528.txt`; workflow screenshots | Fresh live browser test passed login, create, search, edit, and status change. |
| 10. Continuous testing in Jenkins | COMPLETE | `Jenkinsfile`; build #8 actual console and Jenkins test report | Build #8 reported 9 tests: 8 passed, 0 failed, 1 skipped; its dedicated Selenium end-to-end test passed. |
| 11. Docker image and container lifecycle | COMPLETE | `Dockerfile`, `deployment/docker-compose.yml`, `persistence-20261019-115528.txt`, current Docker status | A MySQL-backed test record remained after restarting only the app container, then was removed. MySQL container identity and volume were preserved. MySQL itself was not restarted. |
| 12. Jenkins-Docker continuous deployment | COMPLETE | `Jenkinsfile`; build #8 actual console; live Docker image/health inspection | Build #8's Docker Build and Deploy stages succeeded on the local `main` commit; no public deployment is claimed. |
| 13. Configuration management | COMPLETE | `deployment/ansible/`; Task 13 logs; fresh `ansible-validation-20261019.txt` | Historical corrected run and idempotence evidence pass; fresh inventory and syntax checks pass. |
| 14. Automated provisioning and reliability | COMPLETE | Task 14 playbooks and historical recovery/rollback log; fresh provisioning/idempotence logs | Historical app recovery and unhealthy-candidate rollback passed. Fresh safe provisioning passed twice without service changes (`changed=0`, `failed=0`). |
| 15. Final integration, evidence, and documentation | PARTIAL | This report/index/checklist, fresh logs, ten readable screenshots, build #8 verification record, current app/Docker health | Build #8 stage and test screenshots and Docker Desktop screenshot are present. A separate build #8 summary screenshot is absent; GitHub remote/publication if required and student review remain. |

## Execution evidence map

| Requirement | Artifact or execution record | Verified result and scope |
|---|---|---|
| Maven validation/package | `documentation/task15-evidence/maven-verify-20261019-115032.txt` | `mvn -B verify` passed: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. Automated database is H2. |
| Fresh Selenium end-to-end and persistence | `documentation/task15-evidence/persistence-20261019-115528.txt` | 1 browser test passed. New fixture ID 15 was present in MySQL before and after the app-only restart, then removed; query confirmed zero remaining. |
| Current Docker/app status | `documentation/task15-evidence/docker-current-status-20261019.txt` | Docker client/engine 29.8.0; app and MySQL running/healthy; app health JSON is `{"status":"UP"}`; the named volume is mounted at `/var/lib/mysql`. |
| Jenkins pipeline | `documentation/task15-evidence/jenkins-build-8-results-20261019.txt`; authenticated Jenkins build #8 console and stage view | Build #8 checked out `b98d762bfe621fb263c2a37e1efac47a2c06c34a` from `file:///workspace`, completed in 1m 58s, and finished `SUCCESS`. The console was inspected live; it has not been exported as a full text log. |
| Jenkins tests and deployment | `screenshots/jenkins-build-8-stages.png`; `screenshots/jenkins-build-8-test-results.png`; `documentation/task15-evidence/jenkins-build-8-results-20261019.txt` | Build #8: Maven stage 8 tests, 7 passed and 1 opt-in Selenium skipped; dedicated Selenium test 1 passed; aggregate report 9 total, 8 passed, 0 failed, 1 skipped. Docker Build and Deploy stages succeeded. Historical #7 images are `jenkins-build-7.png.png` and `jenkins-test-results.png.png`. |
| Task 13 and Task 14 Ansible validation | `documentation/task15-evidence/ansible-validation-20261019.txt`; `ansible-provision-20261019-115311.txt`; `ansible-idempotence-20261019-115311.txt` | Compose validation, inventory, and all four playbook syntax checks passed. Safe provisioning and repeat both had `changed=0`, `failed=0`; MySQL remained unchanged. |
| Task 14 recovery/rollback | `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log` | Historical controlled app-only recovery and unhealthy-candidate rollback passed, restoring the known-good image and HTTP 200/UP; MySQL identity/volume preserved. Not rerun during this completion because it deliberately disrupts service. |
| Database backup | Outside repo: `%LOCALAPPDATA%\Gym-Membership-Portal\database-backups\gym_portal-task15-20261019-114451.sql` | Fresh logical dump; SQL database/table definitions, row data, completion marker, and SHA-256 verified. `--no-tablespaces` was needed because `gym_user` lacks `PROCESS`. It was not restored into a second schema; keep this data file private and do not submit it. |
| Git safety | `.gitignore`; `git check-ignore -v .env`; `git ls-files`; final Git checks | `.env` ignored and untracked; tracked-file scan found no `.env`, SQL/backup, key, or certificate files. The seven older screenshots are tracked. The three newer Jenkins #8/Docker screenshots are explicitly allowed by `.gitignore` and are currently untracked, ready to stage. Backups are outside the repository. |
| GitHub | `git remote -v` | No remote exists, so no URL, webhook, or publication is claimed. |
| Final verification record | `documentation/task15-evidence/Task15_Final_Audit_20261019.md`; `jenkins-build-8-results-20261019.txt` | Records the actual environment, tests, persistence, Ansible, evidence, GitHub, and Jenkins build #8 findings. |

## Current validation commands

```powershell
mvn -B verify
mvn -B -DrunSelenium=true -Dtest=MemberPortalSeleniumTest test
./deployment/ansible/Test-Task14.ps1  # historical reliability exercise; intentionally not repeated here
./deployment/scripts/Status.ps1
Invoke-RestMethod http://localhost:8080/actuator/health
```

The current Ansible safe provisioning run was executed through the Docker-based Linux runner, twice, with output saved under `documentation/task15-evidence/`. The first audit on 2026-10-19 correctly recorded Docker Desktop stopped; later in this completion run it was launched from its installed executable. Existing containers and volumes came up without `compose down`, volume deletion, database reset, or image rebuild.

## Screenshots

Ten readable PNG files were visually inspected under `screenshots/`:

- `application.png.png` — original signed-out application page.
- `jenkins-build-7.png.png` — historical build #7 success summary; it is not the stage diagram.
- `jenkins-test-results.png.png` — historical aggregate report: 9 total, 8 passed, 1 skipped, 0 failed.
- `jenkins-build-8-stages.png` — Jenkins #8 stage view; all displayed pipeline stages show success (allowed by `.gitignore`, untracked and ready to stage).
- `jenkins-build-8-test-results.png` — Jenkins #8 aggregate report: 9 total, 8 passed, 1 skipped, 0 failed (allowed by `.gitignore`, untracked and ready to stage).
- `docker-containers.png` — Docker Desktop view showing Docker running and app, MySQL, and Jenkins containers running. Health is separately verified by Docker inspection and HTTP check (allowed by `.gitignore`, untracked and ready to stage).
- `task15-dashboard.png` — live authenticated dashboard with summary counts and a filtered synthetic member.
- `task15-member-registration.png` — live filtered member list/search result.
- `task15-member-edit-form.png` — live update form with synthetic test data.
- `task15-member-workflow.png` — live filtered result showing the synthetic member as `INACTIVE` after the status update.

The Selenium screenshots show the synthetic member used during the recorded workflow; the test record was removed after the persistence check. The older expected names `application.png`, `jenkins-build-7.png`, and `jenkins-test-results.png` do not exist exactly; their actual legacy names end in `.png.png`. Build #8 has stage and test screenshots but no separate build-summary screenshot. `docker-containers.png` does not itself show health details; the saved Docker status evidence and live check cover health.

## Remaining submission work

- Optionally capture a separate Jenkins #8 build-summary screenshot; existing stage and test images already document the pipeline and test result.
- If required by the submission checklist, capture a Docker/health view combining container status, image, and health endpoint. Current text evidence is in `docker-current-status-20261019.txt`.
- Decide whether GitHub is required, create/select a repository in the student's account, then provide the real remote and approval before publishing.
- Review the local commit and submission documents. Do not include `.env` or the SQL backup.

Submit the source, `README.md`, `documentation/Final_Project_Report.md`, `documentation/Evidence_Index.md`, `documentation/Evidence_Checklist.md`, Jenkins/Docker/Ansible configuration, relevant logs, and genuine screenshots. Keep the database backup private and outside the submission. Task 15 remains partial pending the student's review and any GitHub setup required by the assignment.
