# backend_api (Spring Boot)

This container exposes minimal health check endpoints for an Angular frontend to validate end-to-end functionality.

## Running

The backend is expected to run on port **3001**.

### Database configuration (Postgres)

The DB connectivity health check uses the Spring `DataSource`. Configure it via environment variables:

- `SPRING_DATASOURCE_URL` (example: `jdbc:postgresql://localhost:5432/postgres`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## API Endpoints

### `GET /health`

Overall service health.

- **200 OK** when healthy  
  Example response:
```json
{
  "status": "UP",
  "timestamp": "2026-02-02T12:34:56Z",
  "details": {}
}
```

- **503 Service Unavailable** when unhealthy  
  Example response:
```json
{
  "status": "DOWN",
  "timestamp": "2026-02-02T12:34:56Z",
  "details": {
    "error": "..."
  }
}
```

### `GET /status` (DB check)

Database connectivity check.

- **200 OK** when DB reachable  
  Example response:
```json
{
  "database": "UP",
  "timestamp": "2026-02-02T12:34:56Z",
  "details": {}
}
```

- **503 Service Unavailable** when DB not reachable  
  Example response:
```json
{
  "database": "DOWN",
  "timestamp": "2026-02-02T12:34:56Z",
  "details": {
    "error": "Database connection failed"
  }
}
```

### `GET /health/db` (alias)

Alias for `/status`.

## CORS

CORS is configured to allow the Angular frontend origin:

- `http://localhost:3000`

## Database Migrations (Flyway)

### Overview

This backend includes **Flyway scaffolding for database migrations**, but it is **disabled by default** to ensure zero disruption to existing deployments and preview environments.

### Current State

- **Flyway dependency**: Added to `build.gradle`
- **Configuration**: Present in `application.properties` (disabled)
- **Migration folder**: `src/main/resources/db/migration/` (empty, ready for migrations)
- **Runtime behavior**: **Unchanged** - no migrations run unless explicitly enabled

### How to Enable Flyway

#### Option 1: Environment Variable (Recommended)

Add to your `.env` file:

```bash
FLYWAY_ENABLED=true
```

#### Option 2: Spring Profile

Start the application with the `migrations` profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=migrations'
```

Or set via environment:

```bash
SPRING_PROFILES_ACTIVE=migrations
```

### Adopting Flyway on Existing Databases

If your database already has a schema (typical for existing deployments):

1. **Enable baseline mode** in `.env`:

```bash
FLYWAY_ENABLED=true
FLYWAY_BASELINE_ON_MIGRATE=true
FLYWAY_BASELINE_VERSION=1.0.0
```

2. **Start the application**:
   - Flyway creates `flyway_schema_history` table
   - Records baseline at version 1.0.0
   - Does NOT run any migrations (as none exist yet)

3. **Add migrations when ready**:
   - Create `V1.0.1__your_change.sql` in `src/main/resources/db/migration/`
   - Future startups will run pending migrations

### Migration File Structure

Place SQL migration files in:

```
src/main/resources/db/migration/
├── V1.0.0__initial_schema.sql
├── V1.0.1__add_indexes.sql
└── V1.1.0__add_audit_columns.sql
```

**Naming convention**: `V<version>__<description>.sql`

### Why Disabled by Default?

- **Preview safety**: Ephemeral preview environments work without migration setup
- **Gradual adoption**: Teams can enable when ready, no forced migration
- **Backward compatible**: Existing deployments continue working unchanged
- **Explicit control**: Schema changes only occur when intentionally enabled

### Testing Locally with Migrations

To test the full migration flow locally:

1. **Configure your `.env`**:

```bash
# Database connection (adjust for your setup)
DATABASE_URL=postgresql://user:password@localhost:5432/health_check_dev

# Enable Flyway
FLYWAY_ENABLED=true

# For existing local databases, enable baseline
FLYWAY_BASELINE_ON_MIGRATE=true

# Port
PORT=3002
```

2. **Start the application**:

```bash
./gradlew bootRun
```

3. **Check logs** for Flyway execution:

```
INFO  FlywayExecutor : Flyway migration complete
INFO  FlywayExecutor : Schema version: 1.0.0
```

### Production Deployment

For production databases with existing schema:

```bash
# Use pooled connection for app queries
POSTGRES_PRISMA_URL=postgresql://user:pass@host/db?sslmode=require

# Use non-pooled connection for migrations
POSTGRES_URL_NON_POOLING=postgresql://user:pass@host/db?sslmode=require

# Enable Flyway with baseline for existing schema
FLYWAY_ENABLED=true
FLYWAY_BASELINE_ON_MIGRATE=true
FLYWAY_BASELINE_VERSION=1.0.0
```

### Resources

- **Full Adoption Guide**: See `kavia-docs/CodeWiki/Specs/DetailedDesigns/flyway-migration-adoption-guide.md`
- **Migration Examples**: See `src/main/resources/db/migration/README.md`
- **Flyway Documentation**: https://flywaydb.org/documentation/

### Safety Guarantees

✅ **Zero runtime impact when disabled** (default)  
✅ **Existing database initialization code unchanged**  
✅ **No automatic schema modifications**  
✅ **Opt-in activation only**  
✅ **Preview environments unaffected**
