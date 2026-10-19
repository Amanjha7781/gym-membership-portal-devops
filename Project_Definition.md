# CI/CD Pipeline for a Gym Membership Portal

## 1. Problem Statement

Managing gym members manually using registers or spreadsheets can be time-consuming and may lead to incorrect or outdated records. Gym staff need a simple system where they can manage member information, membership status and basic records efficiently.

The proposed Gym Membership Portal will provide a centralized web-based system for managing gym membership records. The project will also demonstrate a CI/CD pipeline that automatically builds, tests and deploys the application.

## 2. Target Users

1. Gym Admin
2. Gym Staff
3. Gym Members

## 3. Stakeholders

1. Gym Owner
2. Gym Administrator
3. Gym Staff
4. Gym Members
5. Development/DevOps Team

## 4. Main Pain Points

- Manual maintenance of member records
- Difficulty searching member information
- Outdated membership status
- Difficulty updating records
- Lack of centralized information
- Manual deployment of application updates
- Higher possibility of human errors

## 5. Objectives

- Create a simple web-based Gym Membership Portal.
- Allow authorized users to create, view, update and search member records.
- Provide membership status management.
- Provide a basic dashboard with membership information.
- Automate application build and testing.
- Implement a CI/CD pipeline for automated deployment.

## 6. Constraints

- The project should be simple enough to run locally.
- The MVP should contain only essential gym-management features.
- The system should use commonly available development tools.
- The project should be suitable for demonstrating CI/CD concepts.
- Development and deployment should be reproducible.

## 7. Success Criteria

The project will be considered successful if:

- Users can create member records.
- Users can view member records.
- Users can update member records.
- Users can search for members.
- Membership status can be managed.
- A dashboard displays basic membership information.
- Automated tests can be executed.
- The application can be built automatically.
- The CI/CD pipeline successfully deploys a working version.

## 8. MVP Scope

The MVP will contain 15 major tasks:

1. Project setup
2. User/interface setup
3. Member registration
4. View member records
5. Update member records
6. Search members
7. Membership plan management
8. Membership status management
9. Dashboard
10. Basic authentication/authorization
11. Database integration
12. Unit testing
13. Build automation
14. CI/CD pipeline configuration
15. Deployment and final testing

## 9. Student and Implementation Notes

Student: AMANKUMAR JHA  
Roll No: 23102C0072

The current MVP implements member creation, viewing, updating and search; membership plans and statuses; an admin/staff login; a summary dashboard; a Spring Boot REST API; JUnit tests; a Selenium browser flow; Docker Compose deployment with MySQL; and a Jenkins pipeline definition. The pipeline is described in `Jenkinsfile`. Runtime passwords are supplied through a local ignored `.env` file or another environment/secret manager and are not committed.

The 15 project tasks and evidence-based statuses are tracked in `documentation/Agile_Planning.md` and `documentation/Evidence_Index.md`. Jenkins builds `#7` and `#8`, Ansible configuration management (Task 13), and automated provisioning/reliability recovery and rollback validation (Task 14) have execution evidence. On 2026-10-19 a fresh Selenium workflow created and removed a test member, and a separate persistence check confirmed the member remained in MySQL after restarting only the application container; the MySQL container and named volume were preserved. Task 4 (GitHub remote) remains partial, as does Task 15 pending final student review and any required GitHub setup. Docker, MySQL, the app, and Jenkins were verified running locally during the 2026-10-19 final audit; the app health endpoint returned HTTP 200 / `UP`. Build #8 stage/test and Docker container screenshots are present. No GitHub URL is claimed.
