# Jenkins CI

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

Jenkins runs in its own Compose project at <http://localhost:8081>. `deployment/jenkins/Dockerfile` extends the official Jenkins LTS JDK 21 image with Maven, Docker CLI, and Docker Compose. The Compose service persists Jenkins state in `gym-portal-jenkins-home`, mounts the project workspace read-only at `/workspace`, and mounts the Docker socket for local image builds/deployment.

## First-time setup

1. Start Jenkins: `docker compose -f deployment/jenkins/compose.yml up -d --build`.
2. Open <http://localhost:8081>, unlock Jenkins with the one-time password displayed by `docker exec gym-portal-jenkins cat /var/jenkins_home/secrets/initialAdminPassword`.
3. Install suggested plugins and continue setup. The browser's first-time admin workflow can be completed locally.
4. Create a **Pipeline** job sourced from the local Git repository mounted at `/workspace` (branch `main`), script path `Jenkinsfile`.
5. Keep the ignored local `.env` file in the project root. The Jenkins pipeline reads it from the read-only workspace mount and passes it to Compose as an env file; it is never added to Git. Do not add the `.env` file to Jenkins console output or build artifacts.
6. Run the job and inspect its stage view and `target/surefire-reports`.

The Jenkinsfile runs checkout, Maven unit/integration tests, Selenium E2E in the Compose profile, and a Docker image build. Deployment is guarded for `main`. Test reports are published even when a test stage fails.

## Docker socket note

Mounting `/var/run/docker.sock` gives Jenkins control over the Docker Desktop engine, equivalent to broad host-level control. Keep this lab instance local and do not expose it to untrusted jobs or networks. Jenkins is not published externally; port 8081 is for local lab use.
