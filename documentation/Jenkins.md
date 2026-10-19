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
3. **Selenium End-to-End** starts app/MySQL and Selenium through Compose using `/workspace/.env` as a Compose env file. It passes only the Compose-resolved `GYM_ADMIN_PASSWORD` to the opt-in browser test, with shell tracing disabled before secret handling. Its JUnit report is published.
4. **Docker Build** builds the packaged application image.
5. **Deploy** runs only when the checked-out commit belongs to `origin/main`, using `docker compose --env-file /workspace/.env -f deployment/docker-compose.yml up -d --build`. Earlier stage failure prevents deployment.

The `.env` file must exist locally, remains ignored by Git, and is mounted read-only. It must not be printed to the Jenkins console or archived as a build artifact.

## Verified run history and current access

The local Pipeline job `Gym-Membership-Portal-CI` uses `file:///workspace` (`*/main`, `Jenkinsfile`) without GitHub credentials. Historical build `#7` checked out `0f6247d7811796ed2c62545c3ff6ffc7346ef46c` and passed Maven validation/tests, Selenium end-to-end, Docker image build, and deployment. The app health endpoint returned HTTP 200 with `{"status":"UP"}`. Build #7 is historical; it does not verify the current working tree or the fresh Task 15 test-evidence changes. Builds `#1`–`#6` are retained in the run history; notable earlier failures are documented below.

On 2026-10-19 Docker Desktop and the persisted Jenkins container were started from their existing installation and Compose state. The job configuration file exists in the retained Jenkins home; build #7 remains `SUCCESS`, and Jenkins reports 8 as the next build number. `http://localhost:8081/login` returns HTTP 200, while an unauthenticated job API request returns HTTP 403. No fresh build was triggered because an authenticated Jenkins session is required. Sign in as the existing Jenkins user and trigger build #8; do not treat the next build number as a completed build. The sanitized observation is in `documentation/task15-evidence/jenkins-current-status-20261019.txt`.
## Docker socket note

Mounting `/var/run/docker.sock` gives Jenkins control over the Docker Desktop engine, equivalent to broad host-level control. Keep this lab instance local and do not expose it to untrusted jobs or networks. Jenkins is not published externally; port 8081 is for local lab use.

## Historical Jenkins verification attempts — 2026-10-09

Build `#2` was aborted before the Selenium stage so the pipeline could be corrected to disable shell tracing before loading `/workspace/.env`. Build `#3` checked out commit `9ae8b5707b62f75fa6c8cfe356aaef603896c479` and completed `mvn -B clean verify`: 8 tests ran, 7 passed, 1 Selenium test was skipped by its opt-in setting, and there were 0 failures or errors. Jenkins recorded the Maven report.

Build `#3` did **not** pass. The Selenium stage's Compose startup found the retained MySQL data volume, then the new app container failed database authentication (`Access denied for user 'gym_user'`). The current local `.env` root value also did not authenticate to that existing MySQL instance. The persistent volume `gym-membership-portal-mysql` remains present and was not reset. The app container is stopped after the failed attempt; MySQL remains healthy. Compose built the app image while preparing the Selenium stack, but the pipeline's separate Docker Build and Deploy stages were skipped, so there is no successful pipeline deployment or health-check result to report.

At the time of build `#3`, recovery had not yet been authorized or performed; the later verified recovery is documented below. No credentials were printed in the Jenkins console.

## Credential recovery and final verification — 2026-10-09

### Database preservation and credential repair

The Compose application values agreed on database `gym_portal` and user `gym_user`; however, Jenkins's Selenium shell had sourced `.env` as shell code before calling Compose. Shell expansion differed from Compose's dotenv parsing, and exported shell variables took precedence over `--env-file`. The app therefore received a different database password from the one stored in the existing MySQL data directory. The pipeline now lets Compose parse `.env` and passes only the Compose-resolved `GYM_ADMIN_PASSWORD` to the Selenium test with tracing disabled.

Before credential repair, MySQL was gracefully stopped and the complete data directory from volume `gym-membership-portal-mysql` was archived outside the Git project at `C:\Users\Divya\AppData\Local\Gym-Membership-Portal\database-backups\gym_portal-volume-20261009-173546.tar.gz`. The archive was fully extracted for verification, contained the `gym_portal` directory and InnoDB data file, and passed a cross-container SHA-256 check. It was rechecked after recovery. The named volume was retained throughout.

A temporary recovery container with networking disabled mounted the same volume. Only `gym_user`@`%` was updated to the application password already resolved by Compose; the MySQL root password was not changed. The helper container was removed, and the original MySQL service was restarted against the same volume. A read-only `SELECT 1` login check passed. The application `.env` file was not modified.

The configured MySQL root password did not authenticate during the earlier diagnostic and was deliberately left unchanged because the app and pipeline require only `gym_user`. Root-level database administration may need separate credential recovery later.

### Jenkins build `#7`

Build `#7` checked out `0f6247d7811796ed2c62545c3ff6ffc7346ef46c` from `origin/main` and finished **SUCCESS**:

- Maven validation/package and unit/integration tests: 8 run, 7 passed, 1 opt-in Selenium test skipped, 0 failures/errors.
- Selenium end-to-end stage: 1 run, 1 passed, 0 failures/errors/skips.
- Docker image `gym-membership-portal:ci`: built successfully.
- Deploy stage: executed on `main`; Compose reported MySQL healthy and kept the app service running.
- App container: running and Docker health `healthy`; its image ID matched `gym-membership-portal:1.0.0` (`sha256:61d188e4fa9210dc0dfc98dbf2c23df2ecca32d0b5214e98816cbbd949072c53`).
- `http://localhost:8080/actuator/health`: HTTP 200 with `{"status":"UP"}`.
- Persistent volume: `gym-membership-portal-mysql`, still mounted at `/var/lib/mysql`; verified backup remained readable.

No secret values were printed to Jenkins output or committed to Git.
