# Final Project Report — CI/CD Pipeline for a Gym Membership Portal

**Student:** AMANKUMAR JHA  
**Roll No:** 23102C0072

## 1. Problem statement and objectives

Registers and spreadsheets can leave gym member data out of date and difficult to search. This project provides a small authenticated web portal to create, review, search, and update membership records, while demonstrating automated build, tests, container packaging, and a Jenkins delivery pipeline.

## 2. Requirements and user stories

The MVP supports admin and staff sign-in, member registration, member list/search, member updates, plan and status workflow, dashboard summaries, REST access, MySQL persistence, automated tests, Docker deployment, and Jenkins stages. Admins can change status; staff can maintain member details. Acceptance criteria and the 15-task plan are in [Agile Planning](Agile_Planning.md); functional and non-functional requirements are in [Requirements and Architecture](Requirements_Architecture.md).

## 3. Architecture and technology

Spring Boot uses a layered controller/service/repository design and a Thymeleaf/CSS frontend. JPA connects to MySQL in Compose. Jenkins drives Maven, Selenium, and Docker. See [Architecture](Architecture.md).

**Stack:** JDK 21, Spring Boot 3.5.8, Maven 3.9.16, MySQL 8.4, Spring Security, Thymeleaf, JUnit 5, Selenium 4.49, Docker Desktop, Jenkins LTS, Git. GitHub remote setup remains separate from this local repository.

## 4. Data model and features

`Member` contains ID, name, unique email, phone, plan, start/end dates, and status. The database schema and environment variables are described in [Database](Database.md). UI and APIs implement member create/read/update/search, dashboard counts, and admin-only status changes.

## 5. Testing

JUnit service and integration tests cover create/retrieve/update/search, validation, role authorization, login redirect, and summary counts. The opt-in Selenium flow exercises login, create, view, search, update, and status change. Maven reports are generated in `target/surefire-reports/`.

Verified during implementation: Maven `clean verify` passed; the Selenium Chrome run passed against the Dockerized app; MySQL 8.4 and the app became healthy; `/actuator/health` returned `UP`. Jenkins has been containerized; its job still needs first-run browser setup and a Jenkins pipeline run before Jenkins results can be claimed. Refer to the current handoff report for up-to-date status.

## 6. Docker, Jenkins and CI/CD

Compose provisions MySQL with a persistent named volume and the Spring Boot app with health checks. Jenkins is a separate container at port 8081. The Jenkinsfile runs Maven verification, browser automation, image build, and a guarded main-branch deploy, reading local lab settings from the ignored `.env` file mounted read-only into Jenkins. Docker guidance and pipeline details are in [Docker](Docker.md), [Jenkins](Jenkins.md), and [CI/CD Workflow](CI_CD_Workflow.md).

## 7. Deployment and results

The local app URL is <http://localhost:8080>; Jenkins is <http://localhost:8081>. This is a local lab setup, not a public internet deployment. Docker-based MySQL/app connectivity and an end-to-end browser flow were tested. Capture the Jenkins job result after first-run setup before submitting screenshots or claiming Jenkins delivery success.

## 8. Limitations and future scope

- Demo accounts are in-memory and reset when the app restarts; there is no account administration or password recovery.
- There are no membership payments, notifications, member self-service, audit trail, or production backup policy.
- GitHub remote/webhook and a public deployment are not configured.
- Hibernate schema update is used for a compact demo; a production system should use versioned migrations.
- Use HTTPS, a managed secret store, stronger operational monitoring, and hardened Jenkins agents for a production deployment.

## 9. Conclusion

The project connects a practical member-management MVP with unit/integration and browser tests, containerized MySQL/app services, health checks, and a Jenkins pipeline. Remaining manual actions are limited to Jenkins first-time administrator setup and, if a GitHub remote is required for assessment, GitHub authentication and repository selection.
