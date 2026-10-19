# Deployment and Local Provisioning

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

## Start and stop on Windows

1. Ensure Docker Desktop is running in Linux-container mode.
2. Create `.env` from `.env.example` and replace every placeholder with a private, unique local password.
3. From the repository root, run:

```powershell
./deployment/scripts/Start-Project.ps1
```

The script runs Maven verification and starts MySQL/app containers. Wait until Compose reports `healthy`; open <http://localhost:8080>. Other helper scripts:

```powershell
./deployment/scripts/Status.ps1
./deployment/scripts/Restart-Project.ps1
./deployment/scripts/Stop-Project.ps1
```

Stopping leaves the MySQL volume intact. Run `mvn clean verify` to run the unit/integration suite. See [Testing](Testing.md) to run Selenium against the deployed app.

## Health, logs, and recovery

```powershell
docker compose -f deployment/docker-compose.yml ps
Invoke-RestMethod http://localhost:8080/actuator/health
docker compose -f deployment/docker-compose.yml logs --tail 100 gym-portal
docker compose -f deployment/docker-compose.yml logs --tail 100 mysql
```

To recover after a restart, use the same `.env`, start the stack, check the health endpoint, and confirm a previously registered member remains in the list. Data is kept in a named volume; no cloud or public deployment is configured.

## Ansible configuration management

`deployment/ansible/` contains the local inventory, playbook, and a Docker-based Ansible runner for Windows/Docker Desktop. It validates Compose configuration and preserves the MySQL volume; it skips Compose changes when the app and database are already healthy. See [Ansible configuration management](../deployment/ansible/README.md) for prerequisites, run commands, evidence logs, and limitations.

Task 14's provisioning, controlled app recovery, and known-good image rollback were executed and verified. The repeatable lab test is `deployment/ansible/Test-Task14.ps1`; it briefly disrupts only the app container as part of the failure exercise. Review its test-scope and safeguards in the Ansible guide before running it while users are active.

## Final verification — 2026-10-19

Docker Desktop engine 29.8.0 is running. The application and MySQL containers report healthy, and `Invoke-RestMethod http://localhost:8080/actuator/health` returns `{"status":"UP"}`. A fresh Selenium fixture was verified in MySQL before and after restarting only `gym-portal`; its row was then deleted and the deletion verified. The MySQL container identity and `gym-membership-portal-mysql:/var/lib/mysql` mount were unchanged. See `documentation/task15-evidence/persistence-20261019-115528.txt` and `docker-current-status-20261019.txt`.

The local Jenkins container and saved job configuration are present. On 2026-10-19, authenticated build #8 checked out commit `b98d762bfe621fb263c2a37e1efac47a2c06c34a` and completed `SUCCESS`; Maven, dedicated Selenium, Docker Build, and Deploy stages passed. The aggregate test report showed 9 total, 8 passed, 0 failed, and 1 skipped. After deployment, both app and MySQL were healthy, the running app image matched `gym-membership-portal:1.0.0`, and `/actuator/health` returned HTTP 200 / `UP`. See `documentation/task15-evidence/jenkins-build-8-results-20261019.txt`.
