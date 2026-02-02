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
