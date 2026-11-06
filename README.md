# Spring Boot 3 Kotlin Demo — RestController

A demo project showcasing modern Spring Boot 3 development with Kotlin using the **annotation-based (@RestController)** style, built on a fully reactive stack.

## Features

- **RestController (Annotation-based Routing)** — Routes defined via `@RestController`, `@GetMapping`, `@PostMapping` and other Spring MVC annotations
 using `CoroutineCrudRepository` for non-blocking persistence
- **Global Exception Handler** — Centralized RFC 9457 Problem Details with localized validation errors and correlation IDs
- **Swagger / OpenAPI** — Auto-generated interactive API documentation via Springdoc
- **Hibernate Validation (i18n)** — Bean validation with localized constraint violation messages
- **Spotless & Linting** — Automated code formatting and style enforcement
- **Testcontainers** — Integration tests using real database containers for reliable, environment-independent testing

## Tech Stack

- Kotlin · Spring Boot 3 · WebFlux · R2DBC · PostgreSQL · Flyway

## Environment Variables

Local development loads `.env` through Spring Dotenv. Copy `.env.example` to `.env` and update these values:

- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_PORT`
- `SERVER_PORT`

## Getting Started

1. Ensure PostgreSQL is running with the database named by `DB_NAME` (default: `demo_spring_kotlin`)
   > 💡 Need a quick local database? Use [github.com/danygiguere/docker_db](https://github.com/danygiguere/docker_db) to spin up a dockerized PostgreSQL instance.
2. Copy `.env.example` to `.env` and set your local PostgreSQL credentials
3. Copy `src/main/resources/application.example.yml` to `src/main/resources/application.yml`, or update your existing local file to use the same environment variables
4. Run the application — Flyway will automatically apply migrations on startup
5. Access Swagger UI at **http://localhost:8083/swagger-ui.html** (port configured by `SERVER_PORT`, default: `8083`)

## Data Model

```
┌─────────────────┐        ┌─────────────────┐
│   Enterprise    │ 1    N │      Team       │
│─────────────────│───────►│─────────────────│
│ id              │        │ id              │
│ name            │        │ enterprise_id   │
│ phoneNumber     │        │ name            │
│ website         │        │ description     │
│ email           │        └────────┬────────┘
│ description     │                 │
└─────────────────┘                 │ N
                                    │
                          ┌─────────┴────────┐
                          │   team_members   │
                          │──────────────────│
                          │ team_id          │
                          │ user_id          │
                          └─────────┬────────┘
                                    │ N
                                    ▼
                           ┌─────────────────┐
                           │      User       │
                           │─────────────────│
                           │ id              │
                           │ name            │
                           │ email           │
                           │ phoneNumber     │
                           └─────────────────┘
```

- **Enterprise → Team**: One-to-Many (a team belongs to one enterprise)
- **Team ↔ User**: Many-to-Many via `team_members` join table

## Testing the API

A Postman collection (`postman_collection.json`) is included at the root of the project — import it to get all endpoints ready to use.

> 🌐 **i18n:** Add the `Accept-Language: fr` header to any request to receive validation error messages in French. Omit it (or use `Accept-Language: en`) for English.

## Commands

| Command | Description |
|---|---|
| `./gradlew clean build` | Format code with Spotless, compile, test and package |
| `./gradlew bootRun` | Start the application |
| `./gradlew test` | Run tests |
| `./gradlew spotlessApply` | Auto-format all source files |
| `./gradlew spotlessCheck` | Check formatting without modifying files (CI) |

