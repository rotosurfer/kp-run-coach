# KP Run Coach

A Spring Boot application for running coaching and training management.

## Database Configuration

This application supports multiple database configurations based on Spring profiles:

### Development (Default)
- **Database**: SQLite
- **Profile**: `dev` (default)
- **Location**: `${user.home}/kp-run-coach-dev.db` (e.g., `C:\Users\YourName\kp-run-coach-dev.db`)
- **Auto-creation**: Database file is created automatically
- **DDL**: `update` (preserves data between restarts)
- **Dialect**: Auto-detected by Hibernate

### Production
- **Database**: PostgreSQL
- **Profile**: `prod`
- **Configuration**: Environment variables (see below)
- **DDL**: `validate` (requires manual schema management)

### Testing
- **Database**: H2 (in-memory)
- **Profile**: `test`
- **Auto-creation**: Creates fresh database for each test run

## Running the Application

### Development Mode (SQLite) - Quick Start
```bash
# Using startup script (Windows)
start-dev.bat
# or
.\start-dev.ps1

# Using Gradle directly
./gradlew bootRun
# or explicitly set development profile
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Production Mode (PostgreSQL)
```bash
# Using startup script (Windows)
start-prod.bat
# or  
.\start-prod.ps1

# Using environment variables
set SPRING_PROFILES_ACTIVE=prod
set DATABASE_URL=jdbc:postgresql://your-host:5432/kpruncoach
set DATABASE_USERNAME=your-username
set DATABASE_PASSWORD=your-password
./gradlew bootRun
```

### Testing
```bash
./gradlew test
```

## Environment Variables

### PostgreSQL (Production)
- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL` - PostgreSQL connection URL (default: `jdbc:postgresql://localhost:5432/kpruncoach`)
- `DATABASE_USERNAME` - Database username (default: `kpruncoach`)
- `DATABASE_PASSWORD` - Database password (default: `password`)

### Other Configuration
- `JWT_SECRET` - JWT signing secret (default provided for development)
- `APP_URL` - Backend URL (default: `http://localhost:9090`)
- `APP_FRONTEND_URL` - Frontend URL (default: `http://localhost:3000`)

## Database Schema Management

### Development (SQLite)
The schema is automatically updated using Hibernate's `ddl-auto=update` setting.

### Production (PostgreSQL)
For production, schema changes should be managed manually or through database migration tools like Flyway or Liquibase. The application uses `ddl-auto=validate` to ensure the schema matches the entity definitions.

To set up the initial PostgreSQL database:
1. Create the database: `CREATE DATABASE kpruncoach;`
2. Run the application once with `ddl-auto=create` to generate initial schema
3. Change back to `ddl-auto=validate` for subsequent runs

## Project Structure

- `src/main/java/com/kpruncoach/` - Main application code
- `src/main/resources/` - Configuration files and static resources
- `src/frontend/` - React frontend application
- `src/test/` - Test code

## Building

### Full Build (Backend + Frontend)
```bash
./gradlew build
```

### Backend Only
```bash
./gradlew bootJar
```

### Frontend Only
```bash
cd src/frontend && npm run build
```

## Dependencies

### Database Drivers
- SQLite JDBC driver (development)
- PostgreSQL JDBC driver (production)
- H2 database (testing)

### Main Framework
- Spring Boot 3.1.0
- Spring Data JPA
- Spring Security
- JWT Authentication
