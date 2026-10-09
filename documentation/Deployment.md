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

