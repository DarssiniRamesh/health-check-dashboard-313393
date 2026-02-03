# Database Migrations

This directory contains Flyway versioned migration scripts for the backend_api database schema.

## Current Status

**Flyway is DISABLED by default.** No migrations will run unless explicitly enabled via environment configuration.

## How to Enable Flyway

### Local Development

Add to your `.env` file:

```bash
FLYWAY_ENABLED=true
```

Or start with Spring profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=migrations'
```

### Preview/Production Environments

Set environment variable:

```bash
FLYWAY_ENABLED=true
```

## Migration File Naming Convention

Flyway migrations must follow this strict naming pattern:

- **Versioned migrations**: `V<version>__<description>.sql`
  - Example: `V1.0.0__initial_schema.sql`
  - Example: `V1.0.1__add_user_email_index.sql`

- **Repeatable migrations**: `R__<description>.sql`
  - Example: `R__create_views.sql`
  - Re-runs whenever content changes

### Version Format

Use semantic versioning: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking schema changes
- **MINOR**: New tables, columns (backward compatible)
- **PATCH**: Indexes, constraints, small adjustments

## Creating Your First Migration

When you're ready to add migrations (currently not included by design):

1. **For new databases**: Create `V1.0.0__initial_schema.sql` with your complete schema DDL

2. **For existing databases**: 
   - Enable baseline mode: `FLYWAY_BASELINE_ON_MIGRATE=true`
   - Create `V1.0.0__initial_schema.sql` matching your current schema
   - On first run, Flyway records baseline and skips V1.0.0
   - Future migrations (V1.0.1+) will execute normally

## Adoption Plan

This scaffolding implements the non-disruptive Flyway adoption strategy documented in:
`kavia-docs/CodeWiki/Specs/DetailedDesigns/flyway-migration-adoption-guide.md`

### Key Principles

- **Opt-in only**: Flyway disabled by default
- **Zero runtime impact**: Existing behavior unchanged
- **Gradual adoption**: Teams adopt migrations when ready
- **Production safe**: No automatic schema changes

## Example Migration

```sql
-- V1.0.1__add_audit_columns.sql
-- Add created_at and updated_at to all tables

ALTER TABLE users 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE customers 
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add indexes for common queries
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_customers_created_at ON customers(created_at);
```

## Resources

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Migration Adoption Guide](../../../../../../kavia-docs/CodeWiki/Specs/DetailedDesigns/flyway-migration-adoption-guide.md)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

## Safety Notes

- **Never modify existing migrations** after they've been deployed
- **Always test migrations** in development/staging first
- **Create backups** before running migrations in production
- **Document rollback strategy** in migration comments
