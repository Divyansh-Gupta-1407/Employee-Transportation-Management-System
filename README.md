# Employee Transportation Management System

The Employee Transportation Management System is an enterprise-grade RESTful backend application built to manage corporate employee commute logistics, shuttle route management, vehicle fleet allocations, driver scheduling, and trip tracking.

Workplace transport management is critical for organizations offering employee commute benefits, late-night shift transportation, or office shuttle services. This system provides centralized control over daily transport operations, ensuring vehicle capacity compliance, driver scheduling, employee seat booking, and administrative oversight.

---

## Technical Overview

The application is built on Spring Boot 3.3 and Java 17 using a layered architectural model (Controller, Service, Repository). It utilizes PostgreSQL as its primary relational store, Redis as an in-memory cache for high-throughput read operations, and Spring Security with JSON Web Tokens (JWT) for stateless authentication and authorization.

### Technology Stack

| Component | Technology | Version | Description |
|---|---|---|---|
| Language | Java | 17 | Core development environment |
| Framework | Spring Boot | 3.3.5 | Application base and dependency injection |
| Security | Spring Security + JJWT | 6.x / 0.12.6 | JWT authentication and role-based access control |
| Relational Database | PostgreSQL | 16 | Data persistence for users, trips, routes, and fleet |
| Cache Engine | Redis | 7.x | Caching for employee profiles, routes, and departments |
| Database Migration | Flyway | 10.x | Schema versioning and automated seed scripts |
| Containerization | Docker & Docker Compose | Latest | Container runtime and multi-container orchestration |
| Testing | JUnit 5, Mockito, Testcontainers | Standard | Unit testing and isolated integration tests |

---

## System Capabilities and Feature Breakdown

### 1. Authentication and Access Control
- User registration and login endpoints returning signed JWT access tokens.
- Role-based authorization distinguishing three privilege tiers: `ROLE_ADMIN`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE`.
- BCrypt password hashing and custom Spring Security filter chains for stateless request verification.
- Exception handlers returning standardized JSON payloads for authentication and authorization errors.

### 2. Employee and Department Management
- Directory management allowing administrative registration and updates of employee profiles.
- Automatic generation of formatted employee identification codes (`EMP00001` sequence).
- Department grouping, assignment, and profile search filtering by name, email, or employee code.
- Redis cache integration `@Cacheable` and `@CacheEvict` to reduce database query load on employee read paths.

### 3. Fleet and Driver Logistics
- **Vehicle Inventory**: Tracking of vehicle registration numbers, models, types (`BUS`, `VAN`, `CAR`, `SHUTTLE`), active operational status, and seating capacities.
- **Driver Management**: Driver profiles linked to employee records, licensing details, expiration dates, and real-time availability toggling.

### 4. Route and Stop Management
- Definition of commute corridors with specified source, destination, total distance (km), and estimated duration.
- Sequence-ordered route stops containing stop names, order numbers, and optional geographic coordinates (latitude and longitude).
- Cached route details for quick lookup during trip creation and passenger booking.

### 5. Trip Scheduling and Execution
- Trip creation combining assigned routes, designated vehicles, and available drivers with planned departure and arrival timestamps.
- Enforcement of pre-trip validation rules (ensuring vehicles are active, drivers are available, and departure precedes arrival).
- Lifecycle state transitions for trips: `SCHEDULED` -> `IN_PROGRESS` -> `COMPLETED` or `CANCELLED`.
- Automatic recording of actual departure time when a trip starts and actual arrival time when a trip completes.

### 6. Passenger Booking
- Seat reservation functionality for employees on scheduled trips.
- Pickup and dropoff stop selection linked to valid route stops.
- Validation checks preventing bookings when a vehicle has reached full seating capacity.
- Duplicate booking prevention ensuring an employee cannot book multiple seats on the same trip.

### 7. Administration and Operational Reporting
- Dashboard metrics displaying total counts for active employees, operational vehicles, active routes, available drivers, and trip status summaries.
- Date-filtered reporting endpoints aggregating completed vs. cancelled trip statistics.

---

## Architecture and Data Flow

```
                           +------------------------+
                           |  HTTP / REST Clients   |
                           +-----------+------------+
                                       |
                                       v
                           +------------------------+
                           |  Spring Security JWT   |
                           +-----------+------------+
                                       |
               +-----------------------+-----------------------+
               |                       |                       |
               v                       v                       v
     +-------------------+   +-------------------+   +-------------------+
     | Auth / Employee   |   | Route / Vehicle   |   | Trip / Booking    |
     |   Controllers     |   |   Controllers     |   |   Controllers     |
     +---------+---------+   +---------+---------+   +---------+---------+
               |                       |                       |
               v                       v                       v
     +-------------------+   +-------------------+   +-------------------+
     | Employee Service  |   | Transport Service |   | Trip Service      |
     +----+---------+----+   +----+---------+----+   +----+---------+----+
          |         |             |         |             |         |
          |         +-----+       |         +-----+       |         +-----+
          v               v       v               v       v               v
   +--------------+  +--------+ +--------------+  +--------+ +--------------+  +--------+
   | PostgreSQL DB|  | Redis  | | PostgreSQL DB|  | Redis  | | PostgreSQL DB|  | Redis  |
   +--------------+  +--------+ +--------------+  +--------+ +--------------+  +--------+
```

---

## API Reference Summary

### Authentication
- `POST /api/auth/register` - Register a new user account.
- `POST /api/auth/login` - Authenticate credentials and receive a JWT token.

### Employee Management
- `GET /api/employees` - Retrieve a paginated list of employees.
- `GET /api/employees/{id}` - Retrieve detailed profile for an employee.
- `POST /api/employees` - Create a new employee profile (Requires `ADMIN` or `MANAGER`).
- `PUT /api/employees/{id}` - Update an employee profile (Requires `ADMIN` or `MANAGER`).
- `DELETE /api/employees/{id}` - Deactivate an employee profile (Requires `ADMIN`).
- `GET /api/employees/search` - Search employees by keyword (name, code, email).
- `GET /api/employees/department/{departmentId}` - List employees belonging to a specific department.

### Department Management
- `GET /api/departments` - List all active departments.
- `POST /api/departments` - Create a new department (Requires `ADMIN`).

### Fleet and Route Management
- `GET /api/vehicles` - List all fleet vehicles.
- `POST /api/vehicles` - Add a new vehicle to the fleet (Requires `ADMIN`).
- `GET /api/routes` - List all transportation routes.
- `GET /api/routes/{id}` - Get route details including ordered stops.
- `POST /api/routes` - Create a route with pickup/dropoff stops (Requires `ADMIN`).
- `GET /api/drivers` - List driver profiles.
- `POST /api/drivers` - Register an employee as a driver (Requires `ADMIN` or `MANAGER`).
- `PATCH /api/drivers/{id}/availability` - Update driver availability status.

### Trip Operations
- `GET /api/trips` - List scheduled and active trips (supports status filtering).
- `GET /api/trips/{id}` - Get trip details including assigned passengers.
- `POST /api/trips` - Schedule a new trip (Requires `ADMIN` or `MANAGER`).
- `PATCH /api/trips/{id}/status` - Update trip state (`IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
- `POST /api/trips/{tripId}/passengers` - Add an employee booking to a trip.
- `DELETE /api/trips/{tripId}/passengers/{passengerId}` - Cancel a passenger booking.

### Administration
- `GET /api/admin/dashboard` - View overall fleet and operational metrics (Requires `ADMIN`).
- `GET /api/admin/reports/trips` - Generate trip analytics by date range (Requires `ADMIN`).

---

## Setup and Installation

### Prerequisites
- Java 17 Development Kit (JDK)
- Apache Maven 3.8+
- Docker Desktop / Docker Engine and Docker Compose

### Running with Docker Compose (Recommended)

To start the full environment including PostgreSQL, Redis, and the Spring Boot application:

```bash
docker-compose up --build
```

The service will run on `http://localhost:8080`.

### Local Execution (Without Docker Container for App)

1. Start PostgreSQL on `localhost:5432` and create a database named `ets_db` with user `ets_user` and password `ets_password`.
2. Start Redis on `localhost:6379`.
3. Execute the Spring Boot build and application runner:

```bash
mvn clean package
java -jar target/employee-transportation-system-0.0.1-SNAPSHOT.jar
```

---

## Database Migrations

Database structure is managed using Flyway migrations located in `src/main/resources/db/migration/`:

- `V1__create_users_and_roles.sql`: Creates `users`, `roles`, and `user_roles` tables.
- `V2__create_departments_and_employees.sql`: Creates `departments` and `employees` tables.
- `V3__create_transportation_tables.sql`: Creates `vehicles`, `routes`, `route_stops`, `drivers`, `trips`, and `trip_passengers` tables.
- `V4__seed_data.sql`: Seeds initial roles (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_EMPLOYEE`), default departments, and initial routes.

---

## Testing

The project contains unit tests using Mockito and integration tests powered by Testcontainers.

To run unit tests:

```bash
mvn test
```

To run integration tests (requires an active Docker daemon):

```bash
mvn verify
```
