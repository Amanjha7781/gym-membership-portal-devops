# Jenkins CI

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

Jenkins runs in its own Compose project at <http://localhost:8081>. `deployment/jenkins/Dockerfile` extends the official Jenkins LTS JDK 21 image with Maven, Docker CLI, and Docker Compose. The Compose service persists Jenkins state in `gym-portal-jenkins-home`, mounts the project workspace read-only at `/workspace`, and mounts the Docker socket for local image builds/deployment.

## Jenkins and source checkout

The current Jenkins Compose service mounts this repository read-only at `/workspace`; it does not need a GitHub remote or network authentication to check out the project. The Jenkins image trusts only `/workspace` and its `.git` directory in protected Git system configuration; `core.autocrlf=true` avoids false changes when the Windows checkout is read from Linux. Jenkins's Git plugin also blocks local-file repositories by default, so this dedicated lab instance enables `hudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT`. Keep this Jenkins instance local and do not run untrusted jobs on it. The app's Compose project is `deployment/docker-compose.yml`; its explicit environment file is `/workspace/.env`.

To create the local job in Jenkins, use **New Item → Pipeline** and configure **Pipeline script from SCM → Git**:

| Setting | Value |
| --- | --- |
| Repository URL | `file:///workspace` |
| Branch specifier | `*/main` |
| Script path | `Jenkinsfile` |

Do not add credentials for this local file URL. Jenkins executes in its container with Maven 3.9.9, Java 21, Docker CLI, and Docker Compose; the shared Docker socket connects to the local Docker Desktop engine.

## Pipeline stages

1. **Checkout** clones the local `main` repository into the Jenkins job workspace.
2. **Build and Unit Tests** runs `mvn -B clean verify`, which validates, tests, and packages the Spring Boot JAR; Surefire results are published.
3. **Selenium End-to-End** loads the ignored `/workspace/.env` without printing it, starts the Compose app/MySQL and Selenium profile, then runs the browser test against those services. Its JUnit report is published.
4. **Docker Build** builds the packaged application image.
5. **Deploy** runs only for `main`, using `docker compose --env-file /workspace/.env -f deployment/docker-compose.yml up -d --build`. Earlier stage failure prevents deployment.

The `.env` file must exist locally, remains ignored by Git, and is mounted read-only. It must not be printed to the Jenkins console or archived as a build artifact.

## Current run status

The job `Gym-Membership-Portal-CI` is configured as a Pipeline from the local Git SCM (`file:///workspace`, branch `*/main`, script `Jenkinsfile`, no credentials). Build `#1` failed before tests because the Git plugin blocks local-file repositories by default. The scoped checkout property and exact Git system trust paths are now in the Jenkins image. After rebuilding Jenkins, `git ls-remote --heads file:///workspace main` succeeded, and Maven/Docker engine access were reverified. Jenkins home and the job configuration were preserved, but the container restart invalidated the browser session. Sign in to Jenkins again and trigger build `#2`; until that completes, there is no successful Jenkins result or Jenkins-driven deployment to report.

The Jenkinsfile runs checkout, Maven unit/integration tests, Selenium E2E in the Compose profile, and a Docker image build. Deployment is guarded for `main`. Test reports are published even when a test stage fails.

## Docker socket note

Mounting `/var/run/docker.sock` gives Jenkins control over the Docker Desktop engine, equivalent to broad host-level control. Keep this lab instance local and do not expose it to untrusted jobs or networks. Jenkins is not published externally; port 8081 is for local lab use.

## Jenkins verification attempt — 2026-10-09

Build `#2` was aborted before the Selenium stage so the pipeline could be corrected to disable shell tracing before loading `/workspace/.env`. Build `#3` checked out commit `9ae8b5707b62f75fa6c8cfe356aaef603896c479` and completed `mvn -B clean verify`: 8 tests ran, 7 passed, 1 Selenium test was skipped by its opt-in setting, and there were 0 failures or errors. Jenkins recorded the Maven report.

Build `#3` did **not** pass. The Selenium stage's Compose startup found the retained MySQL data volume, then the new app container failed database authentication (`Access denied for user 'gym_user'`). The current local `.env` root value also did not authenticate to that existing MySQL instance. The persistent volume `gym-membership-portal-mysql` remains present and was not reset. The app container is stopped after the failed attempt; MySQL remains healthy. Compose built the app image while preparing the Selenium stack, but the pipeline's separate Docker Build and Deploy stages were skipped, so there is no successful pipeline deployment or health-check result to report.

To continue without losing existing database data, first restore `.env` values that match the credentials stored in the existing MySQL volume, or authorize a credential-recovery procedure that preserves that volume. No credentials were printed in the Jenkins console.
