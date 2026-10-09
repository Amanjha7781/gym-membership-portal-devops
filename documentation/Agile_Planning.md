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

## 7. Implementation Mapping and Verification

| Task | Project artifact or evidence |
|---|---|
| 1. Problem definition and scope | `Project_Definition.md` |
| 2. Agile planning and workflow | This document and `documentation/CI_CD_Workflow.md` |
| 3. Requirements and architecture | `Requirements_Architecture.md`, `Architecture.md`, `Database.md`, `API_Documentation.md` |
| 4. Git/GitHub setup | Local Git repository and `.gitignore`; GitHub remote requires account authentication and repository choice |
| 5–6. Application features | Spring Boot MVC/API, Thymeleaf screens, service, JPA repository, Spring Security |
| 7–8. Jenkins CI/deployment | `Jenkinsfile`, `deployment/jenkins/`; Jenkins first-run setup and a build remain to be performed |
| 9–10. Selenium and continuous testing | Opt-in `MemberPortalSeleniumTest` and Jenkins pipeline stage |
| 11–12. Docker lifecycle and delivery | `Dockerfile`, `deployment/docker-compose.yml`, Jenkins Docker stages |
| 13. Configuration management | Environment variables, ignored `.env`, and `.env.example` |
| 14. Provisioning/reliability | Docker Compose health checks, persistent named volume, and scripts in `deployment/scripts/` |
| 15. Integration and documentation | README, architecture/database/API/testing/deployment docs, final report, evidence checklist |

Local JUnit and browser tests plus a Dockerized MySQL/app run were exercised during implementation. Jenkins job execution and GitHub remote setup are not marked done until verified.
