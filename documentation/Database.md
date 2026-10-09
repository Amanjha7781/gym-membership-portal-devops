# Database Design

**Student:** AMANKUMAR JHA · **Roll No:** 23102C0072

## MySQL configuration

Docker Compose provisions MySQL 8.4 with database `gym_portal` and application user `gym_user`. Set `MYSQL_ROOT_PASSWORD` and `DATABASE_PASSWORD` through a local `.env` file or Jenkins credentials. The application connects using `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`. The named Docker volume `gym-membership-portal-mysql` preserves database files when containers are stopped or recreated.

## Schema

Spring Data JPA creates/updates the `members` table for this demonstration (`spring.jpa.hibernate.ddl-auto=update`).

| Column | Type | Rules |
|---|---|---|
| `id` | BIGINT | Primary key, generated |
| `name` | VARCHAR(80) | Required |
| `email` | VARCHAR(120) | Required, unique, email validation |
| `phone` | VARCHAR(20) | Required, simple phone pattern validation |
| `plan` | VARCHAR/enum | `MONTHLY`, `QUARTERLY`, `ANNUAL` |
| `start_date` | DATE | Required |
| `end_date` | DATE | Required; cannot precede start |
| `status` | VARCHAR/enum | `ACTIVE`, `INACTIVE`, `EXPIRED` |

Portal accounts are held in Spring Security's in-memory user store for the MVP; passwords are BCrypt encoded. The `admin` account has `ADMIN`; `staff` has `STAFF`. A production deployment would store user identities and role assignments in the database and support password rotation/recovery.

## Data lifecycle and checks

```powershell
docker compose -f deployment/docker-compose.yml ps
docker compose -f deployment/docker-compose.yml logs mysql
```

Stop and restart without removing the volume:

```powershell
docker compose -f deployment/docker-compose.yml down
docker compose -f deployment/docker-compose.yml up -d
```

Member records remain in the named volume. Do not run `down -v` unless you intend to remove local database data.

