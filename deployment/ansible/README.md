# Ansible Configuration Management

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

The Task 13 `site.yml` records the initial Compose configuration-management run. Task 14 adds a safer operational workflow: `provision.yml` checks Docker, `.env` presence (not its contents), Compose syntax and dependency declarations, MySQL readiness, the existing volume mount, app health, container status, and MySQL container identity. It refuses to proceed if MySQL is unhealthy and, when necessary, starts or reconciles only `gym-portal` with `--no-build --no-deps`. A healthy deployment is left untouched. It never invokes `down`, volume removal, database reset, or an application image build.

## Files

- `inventory.ini` targets only `localhost` using Ansible's local connection. No remote host or SSH credential is involved.
- `site.yml` contains the commented, repeatable configuration and health-check tasks.
- `provision.yml` performs Task 14 preflight, app-only repair, readiness checks, and known-good image snapshotting when the app is healthy.
- `snapshot.yml` requires a healthy app and saves its running image as `gym-membership-portal:task14-last-known-good`.
- `rollback.yml` restores that saved image and Compose configuration on the app service only, then verifies the restored image, HTTP health, MySQL identity, and volume mount. It skips deployment when the saved image is already active and healthy.
- `tasks/` contains shared read-only preflight, image-snapshot, and post-run reliability checks.
- `Test-Task14.ps1` runs the syntax/inventory checks, preflight, app-only restart recovery exercise, failed-image rollback exercise, and repeatability checks with an automatic app-only safety-recovery path.
- `ansible.cfg` selects the inventory and local Python interpreter discovery defaults.
- `Dockerfile` builds a disposable Ansible runner using the Docker CLI image and pinned `ansible-core` package. It does not install Ansible into Windows or alter the Jenkins container.
- `logs/first-run.log` records the actual initial execution (including the failure that was fixed); `logs/verified-rerun.log` records the successful rerun; `logs/idempotence-check.log` records a repeat run. Syntax and inventory checks are recorded in their correspondingly named logs. Checksums from the initial `.env` stat output are redacted, and the corrected playbook disables checksum collection. No `.env` values are logged.
- `task14-evidence/logs/` contains real Maven, Ansible syntax/inventory, and controlled recovery/rollback run output. Earlier `task14-run-*.log` files preserve failed diagnostic attempts; use the latest `task14-run-20261009-224252.log` for the final passing run.

## Architecture and prerequisites

The server in this lab is the local Docker Desktop Linux container engine, reachable from Windows through the Docker CLI/socket. Ansible itself runs in a short-lived Linux container because native Ansible and a general-purpose WSL distribution are not installed. Docker Desktop's internal `docker-desktop` WSL distribution is not used as a general-purpose Ansible host.

Prerequisites:

- Windows with Docker Desktop's Linux-container engine running, Docker CLI, and Docker Compose plugin.
- The project checkout mounted into the runner at `/workspace`, including `.env` (ignored by Git) and the files below.
- Permission to access `/var/run/docker.sock`. This gives the runner broad control of the local Docker engine; use only with this trusted lab project.
- Internet access on the first runner build to fetch the Docker CLI base image and the pinned Ansible Python package. Maven/JDK are needed for application builds, but this playbook does not invoke Maven.

Relevant folders and configuration:

| Path | Purpose |
| --- | --- |
| `src/` | Spring Boot application and tests |
| `deployment/docker-compose.yml` | Defines `mysql`, `gym-portal`, and optional `selenium` services |
| `deployment/ansible/` | Local Ansible inventory, playbook, runner, and execution evidence |
| `deployment/jenkins/` | Separate Jenkins service and its persistent home |
| `Dockerfile` | Packages the verified Spring Boot JAR |
| `.env` | Local ignored runtime passwords and port settings; consumed by Compose, never displayed by this playbook |
| `.env.example` | Placeholder variable names and local configuration template; contains no usable credentials |
| `Jenkinsfile` | Maven, Selenium, Docker build, and guarded deployment pipeline |
| `gym-membership-portal-mysql` volume | Persistent MySQL data mounted at `/var/lib/mysql`; the playbook only inspects and preserves it |

Ports and services:

- Gym Portal: host port `8080` to container port `8080`; health check at `/actuator/health`.
- MySQL 8.4: port `3306` is internal to the Compose network (the Compose service does not publish it on the Windows host).
- MySQL X Protocol: container port `33060`, likewise not published to the host.
- Selenium Grid: optional `e2e` profile, host port `4444`; Jenkins starts/stops it as part of browser testing. This configuration playbook does not manage it.
- Jenkins: separate Compose project at host port `8081`; this playbook does not manage or restart Jenkins.

## Build and run from PowerShell

Run from the project root. The command mounts the project read/write so Ansible can read the inventory and create the requested evidence log, and mounts the Docker socket to manage the existing engine:

```powershell
docker build -t gym-portal-ansible:local deployment/ansible
if ($LASTEXITCODE -ne 0) { throw 'Ansible runner image build failed.' }

$log = 'deployment/ansible/logs/first-run.log'
New-Item -ItemType Directory -Force -Path (Split-Path $log) | Out-Null
docker run --rm `
  --mount "type=bind,source=$((Get-Location).Path),target=/workspace" `
  --mount 'type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock' `
  --workdir /workspace/deployment/ansible `
  --env ANSIBLE_NOCOLOR=1 `
  gym-portal-ansible:local --syntax-check -i inventory.ini site.yml
if ($LASTEXITCODE -ne 0) { throw 'Ansible syntax check failed.' }

docker run --rm `
  --mount "type=bind,source=$((Get-Location).Path),target=/workspace" `
  --mount 'type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock' `
  --workdir /workspace/deployment/ansible `
  --env ANSIBLE_NOCOLOR=1 `
  gym-portal-ansible:local -i inventory.ini site.yml 2>&1 |
  Tee-Object -FilePath $log
$playbookExitCode = $LASTEXITCODE
if ($playbookExitCode -ne 0) { throw "Ansible playbook failed with exit code $playbookExitCode. Inspect $log." }
```

The Task 13 first-run log is retained as project evidence. Do not add secret values or enable shell tracing around `.env`. For later Task 13 runs, choose a new timestamped log file rather than overwriting the first-run evidence.

## Task 14 provisioning and reliability validation

Run from the repository root in PowerShell. The script verifies the Ansible inventory/playbooks and Compose configuration, runs `mvn -B verify` separately as recorded in the Task 14 evidence, and executes provisioning plus controlled recovery tests. It builds the Ansible runner only if missing. It writes a new timestamped log under `deployment/ansible/task14-evidence/logs/` and refuses to clean up the database or test artifacts.

```powershell
./deployment/ansible/Test-Task14.ps1
```

The script first verifies that MySQL is healthy and that its original container uses the existing `gym-membership-portal-mysql` volume. It captures the healthy app image under `gym-membership-portal:task14-last-known-good`, stops only the `gym-portal` service, and runs `provision.yml` to recover it. It then uses a clearly labeled test-only image that sleeps instead of serving HTTP, temporarily tags it as the Compose app image, and confirms Docker marks the candidate unhealthy. `rollback.yml` restores the known-good image and app configuration. A `finally` handler invokes the rollback/provision path if the exercise is interrupted after a test outage begins.

This is a controlled availability test: the app endpoint is briefly unavailable during the stop/restart and deliberately failed-image portions. Do not run it while someone is using the portal. MySQL is never stopped, recreated, or targeted by the test commands; each run compares its container ID before and after and verifies its volume mount. The temporary failed-candidate and known-good image tags remain on the local Docker engine for auditability.

For manual use, save a known-good image before a real deployment, then run rollback if the app candidate fails:

```powershell
$repo = (Get-Location).Path
$runner = @('--rm', '--mount', "type=bind,source=$repo,target=/workspace", '--mount', 'type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock', '--workdir', '/workspace/deployment/ansible', '--env', 'ANSIBLE_NOCOLOR=1', 'gym-portal-ansible:local')
docker run @runner -i inventory.ini snapshot.yml
if ($LASTEXITCODE -ne 0) { throw 'Healthy image snapshot failed; do not deploy.' }

# After a candidate deployment fails:
docker run @runner -i inventory.ini rollback.yml
if ($LASTEXITCODE -ne 0) { throw 'Rollback failed; inspect the output and keep MySQL untouched.' }
```

Both playbooks fail before changing services if the MySQL health/volume preconditions do not pass or if no known-good image exists. `rollback.yml` restores only the app's `gym-membership-portal:1.0.0` image tag and app container; it does not rebuild the image. Verify the logged image ID and the HTTP health response before treating a recovery as successful.

## Expected result and limitations

When services are already healthy, `provision.yml` reports Docker and Compose validation, skips Compose reconciliation, verifies the HTTP health endpoint and service health checks, and finishes with `changed=0`. If the app is stopped or unhealthy while MySQL remains healthy, Compose starts/reconciles only the app without rebuilding the image. If MySQL is unhealthy or the volume mount differs, Ansible exits before changes. A failed health assertion returns a nonzero playbook result and must be diagnosed before claiming success.

This is a single-machine lab configuration task, not remote server provisioning. It does not install Docker Desktop, provision a Windows or Linux VM, modify Jenkins, build a new application release, manage `.env` contents, or configure production credentials/firewalls. Jenkins remains the CI/CD build authority; Ansible provides local preflight, app-only recovery, known-good image rollback, and reliability validation. A retained image tag is local to this Docker engine and is not a substitute for an off-host release registry or database backup.
