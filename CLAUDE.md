# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Personal blog backend built with Spring Boot 4.1.0 and Java 21. This is a solo-maintainer monolith — not microservices. The companion frontend repo (`my-blog-frontend`) uses Vue 3 + TypeScript.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyBlogBackendApplicationTests

# Run a single test method
./mvnw test -Dtest=ClassName#methodName

# Build JAR (for deployment)
./mvnw clean package -DskipTests
```

## Target Package Structure

Base package: `org.example.myblogbackend`

```
org.example.myblogbackend
├── controller     # REST endpoints (@RestController)
├── service        # Business logic
├── mapper / repo  # Data access (JPA or MyBatis-Plus)
├── entity         # DB-mapped domain objects
├── dto            # Request/response objects (separate from entity)
├── config         # CORS, security, JWT filter, etc.
└── common         # Result<T> wrapper, global exception handler (@RestControllerAdvice), utils
```

## Architecture Decisions

**Unified response wrapper**: All endpoints return `Result<T>` with `code / message / data`. The global exception handler (`@RestControllerAdvice`) maps exceptions to this structure — wire this up before any feature work.

**JWT authentication**: Stateless. `POST /api/login` issues a token; a filter/interceptor validates it on protected routes. Passwords are BCrypt-hashed, never plaintext.

**Soft deletes**: Use an `is_deleted` flag rather than physical deletes on article/comment tables.

**CORS**: Frontend dev server runs on `localhost:5173` — configure allowed origins in a `CorsConfig` before wiring up the frontend.

## Planned API Modules

- Articles: paginated list with category/tag filters, detail (with read-count increment), create/update/delete (auth required)
- Categories & Tags: CRUD, read-only for public, write for authenticated
- Comments: guest submission with review flag, nested replies via parent comment ID
- Auth: login endpoint only (single admin user)

## Deployment (Docker Compose)

Production deploys as three containers: `mysql:8`, `eclipse-temurin:17-jre` (Spring Boot jar), `nginx:alpine`. Nginx serves the Vue `dist/` as static files and reverse-proxies `/api/` to the backend. The backend's datasource URL uses the Docker service name `mysql`, not `localhost`.
