# Jenkins CI

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

Jenkins runs in its own Compose project at <http://localhost:8081>. `deployment/jenkins/Dockerfile` extends the official Jenkins LTS JDK 21 image with Maven, Docker CLI, and Docker Compose. The Compose service persists Jenkins state in `gym-portal-jenkins-home`, mounts the project workspace read-only at `/workspace`, and mounts the Docker socket for local image builds/deployment.

## Jenkins and source checkout

The current Jenkins Compose service mounts this repository read-only at `/workspace`; it does not need a GitHub remote or network authentication to check out the project. Git marks only `/workspace` and its `.git` directory as safe local sources, and `core.autocrlf=true` avoids false changes when the Windows checkout is read from Linux. Jenkins's Git plugin also blocks local-file repositories by default, so this dedicated lab instance enables `hudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT`. Keep this Jenkins instance local and do not run untrusted jobs on it. The app's Compose project is `deployment/docker-compose.yml`; its explicit environment file is `/workspace/.env`.

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

The Jenkins service, `/workspace` mount, local Git visibility, Maven, and Docker engine connection were verified. The first job attempt (`#1`) stopped at checkout because the Git plugin's local-repository security guard was enabled. The local checkout property was then added to the Jenkins Compose configuration; rerun the job after restarting Jenkins to apply it. Do not claim a Jenkins build result until the job has completed successfully.

The Jenkinsfile runs checkout, Maven unit/integration tests, Selenium E2E in the Compose profile, and a Docker image build. Deployment is guarded for `main`. Test reports are published even when a test stage fails.

## Docker socket note

Mounting `/var/run/docker.sock` gives Jenkins control over the Docker Desktop engine, equivalent to broad host-level control. Keep this lab instance local and do not expose it to untrusted jobs or networks. Jenkins is not published externally; port 8081 is for local lab use.
