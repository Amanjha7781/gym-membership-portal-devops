# Agile Planning and DevOps Workflow
## CI/CD Pipeline for a Gym Membership Portal

## 1. User Stories

### User Story 1 – Register Member
As a gym staff member, I want to add a new gym member so that their membership information can be stored in the system.

Acceptance Criteria:
- Staff can enter member name, email and phone number.
- Staff can select a membership plan.
- The system validates required fields.
- A successful registration creates a member record.

### User Story 2 – View Members
As a gym staff member, I want to view all registered members so that I can manage gym records.

Acceptance Criteria:
- The system displays a list of members.
- Member details are visible.
- Membership status is displayed.

### User Story 3 – Update Member
As a gym staff member, I want to update member information so that records remain accurate.

Acceptance Criteria:
- Staff can select an existing member.
- Staff can modify member details.
- Updated information is saved successfully.

### User Story 4 – Search Member
As a gym staff member, I want to search for a member so that I can quickly find their record.

Acceptance Criteria:
- Search can be performed using member name or ID.
- Matching records are displayed.
- A message is shown when no member is found.

### User Story 5 – Manage Membership Status
As a gym administrator, I want to change membership status so that active and inactive memberships can be tracked.

Acceptance Criteria:
- Membership status can be updated.
- The system displays the current status.
- Status changes are stored correctly.

### User Story 6 – View Dashboard
As a gym administrator, I want to see a dashboard so that I can understand the current membership situation.

Acceptance Criteria:
- Dashboard displays total members.
- Dashboard displays active memberships.
- Dashboard displays inactive/expired memberships.

### User Story 7 – User Authentication
As a gym administrator, I want users to log in securely so that only authorized users can access the portal.

Acceptance Criteria:
- Login requires valid credentials.
- Invalid credentials are rejected.
- Authorized users can access the portal.

## 2. Product Backlog

Priority 1 – Project setup and requirements
Priority 2 – User interface
Priority 3 – Member registration
Priority 4 – View member records
Priority 5 – Update member records
Priority 6 – Search members
Priority 7 – Membership plan management
Priority 8 – Membership status management
Priority 9 – Dashboard
Priority 10 – Authentication
Priority 11 – Database integration
Priority 12 – Unit testing
Priority 13 – Build automation
Priority 14 – CI/CD pipeline
Priority 15 – Deployment and final testing

## 3. 15-Task Kanban / Scrum Plan

Task 1  - Problem definition and scope
Task 2  - Agile planning and DevOps workflow
Task 3  - Requirements and architecture
Task 4  - Git and GitHub repository setup
Task 5  - First feature development
Task 6  - Complete MVP features
Task 7  - Jenkins installation and CI setup
Task 8  - Jenkinsfile and server deployment
Task 9  - Selenium test design
Task 10 - Continuous testing in Jenkins
Task 11 - Docker image and container lifecycle
Task 12 - Jenkins-Docker continuous deployment
Task 13 - Configuration management
Task 14 - Automated provisioning and reliability validation
Task 15 - Final integration, testing and documentation

## 4. Definition of Done

A task will be considered Done when:

- Required functionality is implemented.
- Code is saved in the Git repository.
- Code follows the project structure.
- Required tests are completed.
- No critical errors remain.
- The feature works correctly in the local environment.
- Changes are committed with a meaningful commit message.
- Required documentation is updated.
- The feature is ready for integration into the main development workflow.

## 5. DevOps Lifecycle

The project will follow this lifecycle:

Plan
  ↓
Develop
  ↓
Git Commit
  ↓
Build
  ↓
Automated Testing
  ↓
Docker Build
  ↓
Continuous Integration
  ↓
Deployment
  ↓
Monitoring / Feedback
  ↓
Plan

## 6. Kanban Workflow

BACKLOG
   ↓
TODO
   ↓
IN PROGRESS
   ↓
TESTING
   ↓
REVIEW
   ↓
DONE

## 7. Implementation Mapping and Verified Status

See [`Evidence_Index.md`](Evidence_Index.md) for the full assignment-by-assignment evidence and the exact validation records. Current status is:

| Task | Status | Evidence / remaining item |
|---|---|---|
| 1. Problem definition and scope | COMPLETE | `Project_Definition.md`. |
| 2. Agile planning and workflow | COMPLETE | This plan and `CI_CD_Workflow.md`. |
| 3. Requirements and architecture | COMPLETE | Requirements, architecture, database, and API documentation plus `src/`. |
| 4. Git and GitHub setup | PARTIAL | Local Git repository and meaningful commit history exist; `git remote -v` is empty. GitHub setup needs the student's account and repository choice. |
| 5. First feature development | COMPLETE | Member creation implemented and covered by integration/browser tests. |
| 6. Complete MVP | COMPLETE | Member management, search, status, dashboard, authentication, and persistence implemented; automated suite passed. |
| 7. Jenkins installation and CI | COMPLETE | Local Jenkins job build #7 finished `SUCCESS` in the Jenkins UI. |
| 8. Jenkinsfile and deployment | COMPLETE | Build #7 executed pipeline deployment; app health was HTTP 200 / `UP`. |
| 9. Selenium design | COMPLETE | Selenium test source exists; build #7 Selenium stage passed 1 test. |
| 10. Continuous testing | COMPLETE | Jenkins #7 reports Maven tests and Selenium result. |
| 11. Docker image and container lifecycle | COMPLETE | Docker Compose app/MySQL containers and image were verified healthy. A real MySQL-backed fixture remained present after an app-only container restart; the MySQL container and named volume were unchanged. No MySQL container restart was performed. |
| 12. Jenkins-Docker delivery | COMPLETE | Jenkins #7 Docker build and Compose deploy stages passed. |
| 13. Configuration management | COMPLETE | Corrected Ansible run and repeatability logs passed; initial failed attempt is retained as such. |
| 14. Automated provisioning and reliability | COMPLETE | Task 14 app recovery, known-good image rollback, health, volume preservation, and idempotence passed. |
| 15. Final evidence and submission preparation | PARTIAL | Final report/index/checklist, fresh Maven/Selenium/Ansible logs, ten readable screenshots, and verified Jenkins build #8 are present. A separate build #8 summary image is absent; GitHub remote if required and final submission review remain. |

### Task 13 — Configuration management verification

The Ansible playbook in `deployment/ansible/` targets the local Docker Desktop engine through a disposable Linux runner because native Ansible and a general-purpose WSL distribution are unavailable. Inventory and playbook syntax checks are logged separately. The initial Task 13 execution exposed a playbook filter error before any Compose action; it was fixed and retained as redacted evidence. The verified rerun and repeatability check are also logged. They found MySQL and the app healthy and skipped Compose reconciliation. Task 13 is complete.

### Task 14 — Automated provisioning and reliability validation

Task 14 is implemented in `deployment/ansible/provision.yml`, `snapshot.yml`, `rollback.yml`, and `Test-Task14.ps1`. The final run log is `deployment/ansible/task14-evidence/logs/task14-run-20261009-224252.log`; Maven results are in `maven-verify.log`. Ansible validated Compose configuration, the `gym-portal` dependency on healthy MySQL, the existing persistent volume mount, container health, the app endpoint, and unchanged MySQL container identity. A controlled test stopped and restarted only the app and restored health. A deliberately unhealthy test image was deployed to the app service; rollback restored the known-good image ID and HTTP 200 / UP. A repeated rollback and provisioning run each reported `changed=0`, `failed=0`. MySQL was never stopped or recreated. Earlier test-script troubleshooting logs are retained in the same evidence directory and are not presented as passing runs.

Task 14 is complete based on its recorded 2026-10-09 execution. On 2026-10-19, the app-only recovery/persistence flow was freshly verified: the Selenium fixture was written to MySQL, remained after restarting only `gym-portal`, and was then removed; MySQL stayed healthy on the same named volume. The Task 14 safe provisioning playbook and its repeat run also passed with `changed=0, failed=0`. Jenkins build #8 completed successfully with Maven, Selenium, Docker Build, and Deploy stages; its report shows 9 total, 8 passed, 0 failed, and 1 skipped. Genuine screenshots for Jenkins #8 stages/tests and Docker containers are present. Task 15 remains **PARTIAL** pending student review and any required GitHub account/repository work; a separate build summary screenshot is optional unless the rubric requires it. Do not mark the overall assignment complete yet.
