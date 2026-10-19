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
| 7. Jenkins installation and CI setup | COMPLETE | `deployment/jenkins/`; persisted `Gym-Membership-Portal-CI` job; build #7 | Build #7 is historically successful. Jenkins is currently running; a new build needs an authenticated Jenkins session. |
| 8. Jenkinsfile and server deployment | COMPLETE | `Jenkinsfile`; build #7 historical console; current Docker status evidence | Build #7 completed Maven, image build, and Compose deployment. A fresh Jenkins deployment is not claimed. |
| 9. Selenium test design | COMPLETE | `MemberPortalSeleniumTest.java`; `persistence-20261019-115528.txt`; workflow screenshots | Fresh live browser test passed login, create, search, edit, and status change. |
| 10. Continuous testing in Jenkins | COMPLETE | `Jenkinsfile`; historical build #7 console and test-results screenshot | Build #7 passed its Maven and Selenium stages. Fresh local Maven and Selenium tests passed; no fresh Jenkins build is claimed. |
| 11. Docker image and container lifecycle | COMPLETE | `Dockerfile`, `deployment/docker-compose.yml`, `persistence-20261019-115528.txt`, current Docker status | A MySQL-backed test record remained after restarting only the app container, then was removed. MySQL container identity and volume were preserved. MySQL itself was not restarted. |
| 12. Jenkins-Docker continuous deployment | COMPLETE | `Jenkinsfile`; build #7 historical console | Build #7 deployed through Compose on the local main branch. This is not a public deployment. |
| 13. Configuration management | COMPLETE | `deployment/ansible/`; Task 13 logs; fresh `ansible-validation-20261019.txt` | Historical corrected run and idempotence evidence pass; fresh inventory and syntax checks pass. |
| 14. Automated provisioning and reliability | COMPLETE | Task 14 playbooks and historical recovery/rollback log; fresh provisioning/idempotence logs | Historical app recovery and unhealthy-candidate rollback passed. Fresh safe provisioning passed twice without service changes (`changed=0`, `failed=0`). |
| 15. Final integration, evidence, and documentation | PARTIAL | This report/index/checklist, fresh logs, seven screenshots, current app/Docker health | Fresh Jenkins build and stage-view screenshot require interactive sign-in; Docker GUI screenshot, GitHub remote/publication, and final student review remain. |

## Execution evidence map

| Requirement | Artifact or execution record | Verified result and scope |
|---|---|---|
| Maven validation/package | `documentation/task15-evidence/maven-verify-20261019-115032.txt` | `mvn -B verify` passed: 8 tests, 0 failures, 0 errors, 1 opt-in Selenium test skipped. Automated database is H2. |
| Fresh Selenium end-to-end and persistence | `documentation/task15-evidence/persistence-20261019-115528.txt` | 1 browser test passed. New fixture ID 15 was present in MySQL before and after the app-only restart, then removed; query confirmed zero remaining. |
| Current Docker/app status | `documentation/task15-evidence/docker-current-status-20261019.txt` | Docker client/engine 29.8.0; app and MySQL running/healthy; app health JSON is `{"status":"UP"}`; the named volume is mounted at `/var/lib/mysql`. |
| Jenkins pipeline | `documentation/task15-evidence/jenkins-current-status-20261019.txt`; historical Jenkins build #7 | Persisted job config exists; build #7 result is `SUCCESS`; next build number is 8. Current login page returns HTTP 200, anonymous job API returns 403. No new build was triggered. Build #7 console was inspected on 2026-10-10 and is not exported to this repo. |
| Jenkins tests and deployment | Build #7 console; `screenshots/jenkins-test-results.png.png`; `screenshots/jenkins-build-7.png.png` | Historical Maven stage: 8 tests, 7 passed, 1 skipped; Selenium stage: 1 passed; Docker build/deploy completed. Images are historical evidence, not a fresh run. |
| Task 13 and Task 14 Ansible validation | `documentation/task15-evidence/ansible-validation-20261019.txt`; `ansible-provision-20261019-115311.txt`; `ansible-idempotence-20261019-115311.txt` | Compose validation, inventory, and all four playbook syntax checks passed. Safe provisioning and repeat both had `changed=0`, `failed=0`; MySQL remained unchanged. |
| Task 14 recovery/rollback | `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log` | Historical controlled app-only recovery and unhealthy-candidate rollback passed, restoring the known-good image and HTTP 200/UP; MySQL identity/volume preserved. Not rerun during this completion because it deliberately disrupts service. |
| Database backup | Outside repo: `%LOCALAPPDATA%\Gym-Membership-Portal\database-backups\gym_portal-task15-20261019-114451.sql` | Fresh logical dump; SQL database/table definitions, row data, completion marker, and SHA-256 verified. `--no-tablespaces` was needed because `gym_user` lacks `PROCESS`. It was not restored into a second schema; keep this data file private and do not submit it. |
| Git safety | `.gitignore`; `git check-ignore -v .env`; `git ls-files`; final Git checks | `.env` ignored and untracked; backups outside repository. The seven evidence screenshots are explicitly included in Git ignore exceptions; no credential values are intentionally added. |
| GitHub | `git remote -v` | No remote exists, so no URL, webhook, or publication is claimed. |
| Final verification record | `documentation/task15-evidence/Task15_Final_Audit_20261019.md` | Records the actual environment, tests, persistence, Ansible, evidence, GitHub, and Jenkins findings for the completed audit. |

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

Verified readable PNG files under `screenshots/`:

- `application.png.png` — original signed-out application page.
- `jenkins-build-7.png.png` — historical build #7 success summary; it is not the stage diagram.
- `jenkins-test-results.png.png` — historical aggregate report: 9 total, 8 passed, 1 skipped, 0 failed.
- `task15-dashboard.png` — live authenticated dashboard with summary counts and a filtered synthetic member.
- `task15-member-registration.png` — live filtered member list/search result.
- `task15-member-edit-form.png` — live update form with synthetic test data.
- `task15-member-workflow.png` — live filtered result showing the synthetic member as `INACTIVE` after the status update.

The Selenium screenshots show only the new synthetic member; that record was removed after the persistence check. No Docker Desktop/terminal screenshot or Jenkins Pipeline Overview screenshot has been captured.

## Remaining submission work

- Sign in to Jenkins at <http://localhost:8081>, trigger build #8, and capture its actual console, stage overview, and test results. Do not report build #8 until it has run.
- Capture a genuine Docker view showing the healthy app and MySQL containers, image, and health endpoint.
- Decide whether GitHub is required, create/select a repository in the student's account, then provide the real remote and approval before publishing.
- Review the local commit and submission documents. Do not include `.env` or the SQL backup.

Submit the source, `README.md`, `documentation/Final_Project_Report.md`, `documentation/Evidence_Index.md`, `documentation/Evidence_Checklist.md`, Jenkins/Docker/Ansible configuration, relevant logs, and genuine screenshots. Keep the database backup private and outside the submission. Task 15 remains partial until the remaining authenticated Jenkins evidence and student review are complete.
