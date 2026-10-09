# Docker Guide

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

The app image uses a Java 21 JRE and runs as a non-root `gym` user. Maven compiles the JAR first; Docker packages that verified JAR. Compose runs the portal and MySQL, waits for the database health check, exposes the app on port 8080, and stores database files in the named volume `gym-membership-portal-mysql`.

## First start

```powershell
Copy-Item .env.example .env
notepad .env  # Enter private values; do not commit this file.
mvn clean verify
docker compose -f deployment/docker-compose.yml up -d --build
```

## Lifecycle commands

```powershell
docker compose -f deployment/docker-compose.yml ps
docker compose -f deployment/docker-compose.yml logs -f gym-portal
docker compose -f deployment/docker-compose.yml restart
docker compose -f deployment/docker-compose.yml down
```

`down` preserves database data. `down -v` deletes the named database volume, so it is intentionally excluded from routine commands.

## Image and browser test

```powershell
mvn -DskipTests package
docker build -t gym-membership-portal:1.0.0 .
docker compose --profile e2e -f deployment/docker-compose.yml up -d selenium
```

The `e2e` profile adds Selenium standalone Chrome at port 4444. It is used in Jenkins for browser automation; it is not started during ordinary app use.

